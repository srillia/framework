package io.gaad.infrastructure.rpc.client;

import io.gaad.infrastructure.rpc.config.CustomRabbitmqConfirmCallback;
import io.gaad.infrastructure.rpc.annotation.RpcClient;
import io.gaad.infrastructure.rpc.config.CustomRabbitmqReturnCallback;
import io.gaad.infrastructure.rpc.entity.RpcType;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.UUID;

/**
 * RpcClientProxyFactory
 *
 * @author toby
 */
public class RpcClientProxyFactory implements FactoryBean, BeanFactoryAware {

    private BeanFactory beanFactory;
    private Class<?> rpcClientInterface;
    private ConnectionFactory connectionFactory;
    private DirectExchange syncReplyDirectExchange;

    public RpcClientProxyFactory(Class<?> rpcClientInterface) {
        this.rpcClientInterface = rpcClientInterface;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getObject() throws Exception {
        RabbitTemplate sender;
        SimpleMessageListenerContainer replyMessageListenerContainer = null;
        RpcClient rpcClient = this.rpcClientInterface.getAnnotation(RpcClient.class);
        String rpcName = rpcClient.value();
        int replyTimeout = rpcClient.replyTimeout();
        int maxAttempts = rpcClient.maxAttempts();

        //初始化同步队列
        Queue replyQueue = replyQueue(rpcName, UUID.randomUUID().toString());
        replyBinding(rpcName, replyQueue);
        RabbitTemplate syncSender = syncSender(rpcName, replyQueue, replyTimeout, maxAttempts, getConnectionFactory());
        replyMessageListenerContainer = replyMessageListenerContainer(rpcName, replyQueue, syncSender, getConnectionFactory());
        //初始化异步队列
        RabbitTemplate asyncSender = asyncSender(rpcName, getConnectionFactory());

        return Proxy.newProxyInstance(this.rpcClientInterface.getClassLoader(), new Class[]{this.rpcClientInterface}, new RpcClientProxy(this.rpcClientInterface, rpcName,syncSender,asyncSender, replyMessageListenerContainer));
    }

    @Override
    public Class<?> getObjectType() {
        return this.rpcClientInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 实例化 replyQueue
     */
    private Queue replyQueue(String rpcName, String rabbitClientId) {
        return registerBean(RpcType.SYNC.getName() + "-ReplyQueue-" + rpcName, Queue.class, rpcName + ".reply." + rabbitClientId, false, false, true);
    }

    /**
     * 实例化 ReplyBinding
     */
    private void replyBinding(String rpcName, Queue queue) {
        registerBean(RpcType.SYNC.getName() + "-ReplyBinding-" + rpcName, Binding.class, queue.getName(), Binding.DestinationType.QUEUE, getSyncReplyDirectExchange().getName(), queue.getName(), Collections.<String, Object>emptyMap());
    }

    /**
     * 实例化 ReplyMessageListenerContainer
     */
    private SimpleMessageListenerContainer replyMessageListenerContainer(String rpcName, Queue queue, RabbitTemplate syncSender, ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer replyMessageListenerContainer = registerBean(RpcType.SYNC.getName() + "-ReplyMessageListenerContainer-" + rpcName, SimpleMessageListenerContainer.class, connectionFactory);
        replyMessageListenerContainer.setQueueNames(queue.getName());
        replyMessageListenerContainer.setMessageListener(syncSender);
        return replyMessageListenerContainer;
    }

    /**
     * 实例化 AsyncSender
     */
    private RabbitTemplate asyncSender(String rpcName, ConnectionFactory connectionFactory) {
        RabbitTemplate asyncSender = registerBean(RpcType.ASYNC.getName() + "-Sender-" + rpcName, RabbitTemplate.class, connectionFactory);
        asyncSender.setDefaultReceiveQueue(rpcName + ".async");
        asyncSender.setRoutingKey(rpcName + ".async");
        CustomRabbitmqConfirmCallback customRabbitmqConfirmCallback = new CustomRabbitmqConfirmCallback();
        asyncSender.setConfirmCallback(customRabbitmqConfirmCallback);
        CustomRabbitmqReturnCallback customRabbitmqReturnCallback = new CustomRabbitmqReturnCallback();
        asyncSender.setReturnCallback(customRabbitmqReturnCallback);
        return asyncSender;
    }

    /**
     * 实例化 SyncSender
     */
    private RabbitTemplate syncSender(String rpcName, Queue replyQueue, int replyTimeout, int maxAttempts, ConnectionFactory connectionFactory) {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(maxAttempts);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        RabbitTemplate syncSender = registerBean(RpcType.SYNC.getName() + "-Sender-" + rpcName, RabbitTemplate.class, connectionFactory);
        syncSender.setDefaultReceiveQueue(rpcName);
        syncSender.setRoutingKey(rpcName);
        syncSender.setReplyAddress(replyQueue.getName());
        syncSender.setReplyTimeout(replyTimeout);
        syncSender.setRetryTemplate(retryTemplate);
        return syncSender;
    }

    /**
     * 实例化 ConnectionFactory
     */
    private ConnectionFactory getConnectionFactory() {
        if (this.connectionFactory == null) {
            this.connectionFactory = this.beanFactory.getBean(ConnectionFactory.class);
        }
        return this.connectionFactory;
    }

    /**
     * 实例化 SyncReplyDirectExchange
     */
    private DirectExchange getSyncReplyDirectExchange() {
        if (this.syncReplyDirectExchange == null) {
            this.syncReplyDirectExchange = registerBean("syncReplyDirectExchange", DirectExchange.class, "simple.rpc.sync.reply", true, false);
        }
        return this.syncReplyDirectExchange;
    }

    /**
     * 对象实例化并注册到Spring上下文
     */
    private <T> T registerBean(String name, Class<T> clazz, Object... args) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                beanDefinitionBuilder.addConstructorArgValue(arg);
            }
        }
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) this.beanFactory;
        if (beanDefinitionRegistry.isBeanNameInUse(name)) {
//            throw new RuntimeException("BeanName: " + name + " 重复");
            return this.beanFactory.getBean(name, clazz);
        }
        beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
        return this.beanFactory.getBean(name, clazz);
    }

}
