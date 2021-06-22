package io.gaad.infrastructure.rpc.server;

import io.gaad.infrastructure.rpc.annotation.RpcServer;
import io.gaad.infrastructure.rpc.entity.RpcType;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * RpcServerPostProcessor
 *
 * @author toby
 */
@Component
public class RpcServerPostProcessor implements BeanPostProcessor {

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    @Lazy
    private ConnectionFactory connectionFactory;
    private DirectExchange syncDirectExchange;
    private DirectExchange asyncDirectExchange;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> rpcServerClass = bean.getClass();
        if (rpcServerClass.getAnnotations() != null && rpcServerClass.getAnnotations().length > 0) {
            for (Annotation annotation : rpcServerClass.getAnnotations()) {
                if (annotation instanceof RpcServer) {
                    rpcServerStart(bean, (RpcServer) annotation);
                }
            }
        } else {
            if (rpcServerClass.getName().contains("CGLIB")) {//cglib代理时，取其父类的注解
                Annotation[] annotations = rpcServerClass.getSuperclass().getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof RpcServer) {
                        rpcServerStart(bean, (RpcServer) annotation);
                    }
                }
            }
        }
        return bean;
    }

    /**
     * 启动服务监听
     */
    private void rpcServerStart(Object rpcServerBean, RpcServer rpcServer) {
        String rpcName = rpcServer.value();
        for (RpcType rpcType : rpcServer.type()) {
            switch (rpcType) {
                case SYNC:
                    Map<String, Object> params = new HashMap<>(1);
                    params.put("x-message-ttl", rpcServer.xMessageTTL());
                    Queue syncQueue = queue(rpcName, rpcType, params);
                    binding(rpcName, rpcType, syncQueue);
                    RpcServerHandler syncServerHandler = rpcServerHandler(rpcName, rpcType, rpcServerBean);
                    messageListenerContainer(rpcName, rpcType, syncQueue, syncServerHandler, rpcServer.threadNum());
                    break;
                case ASYNC:
                    Queue asyncQueue = queue(rpcName, rpcType, null);
                    binding(rpcName, rpcType, asyncQueue);
                    RpcServerHandler asyncServerHandler = rpcServerHandler(rpcName, rpcType, rpcServerBean);
                    messageListenerContainer(rpcName, rpcType, asyncQueue, asyncServerHandler, rpcServer.threadNum());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 实例化 Queue
     */
    private Queue queue(String rpcName, RpcType rpcType, Map<String, Object> params) {
        return registerBean(this.applicationContext, rpcType.getName() + "-Queue-" + rpcName, Queue.class, rpcType == RpcType.ASYNC ? (rpcName + ".async") : rpcName, true, false, false, params);
    }

    /**
     * 实例化 Binding
     */
    private void binding(String rpcName, RpcType rpcType, Queue queue) {
        registerBean(this.applicationContext, rpcType.getName() + "-Binding-" + rpcName, Binding.class, queue.getName(), Binding.DestinationType.QUEUE, getDirectExchange(rpcType).getName(), queue.getName(), Collections.<String, Object>emptyMap());
    }

    /**
     * 实例化 RpcServerHandler
     */
    private RpcServerHandler rpcServerHandler(String rpcName, RpcType rpcType, Object rpcServerBean) {
        return registerBean(this.applicationContext, rpcType.getName() + "-RpcServerHandler-" + rpcName, RpcServerHandler.class, rpcServerBean, rpcName, rpcType);
    }

    /**
     * 实例化 SimpleMessageListenerContainer
     */
    private void messageListenerContainer(String rpcName, RpcType rpcType, Queue queue, RpcServerHandler rpcServerHandler, int threadNum) {
        SimpleMessageListenerContainer messageListenerContainer = registerBean(this.applicationContext, rpcType.getName() + "-MessageListenerContainer-" + rpcName, SimpleMessageListenerContainer.class, this.connectionFactory);
        messageListenerContainer.setQueueNames(queue.getName());
        messageListenerContainer.setMessageListener(rpcServerHandler);
        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        messageListenerContainer.setConcurrentConsumers(threadNum);
    }

    private DirectExchange getDirectExchange(RpcType rpcType) {
        if (rpcType == RpcType.SYNC) {
            if (this.syncDirectExchange == null) {
                this.syncDirectExchange = registerBean(this.applicationContext, "syncDirectExchange", DirectExchange.class, "simple.rpc.sync", true, false);
            }
            return this.syncDirectExchange;
        }
        if (this.asyncDirectExchange == null) {
            this.asyncDirectExchange = registerBean(this.applicationContext, "asyncDirectExchange", DirectExchange.class, "simple.rpc.async", true, false);
        }
        return this.asyncDirectExchange;
    }

    private <T> T registerBean(ConfigurableApplicationContext applicationContext, String name, Class<T> clazz, Object... args) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        if (args.length > 0) {
            for (Object arg : args) {
                beanDefinitionBuilder.addConstructorArgValue(arg);
            }
        }
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) (applicationContext).getBeanFactory();
        if (beanFactory.isBeanNameInUse(name)) {
            throw new RuntimeException("BeanName: " + name + " 重复");
        }
        beanFactory.registerBeanDefinition(name, beanDefinition);
        return applicationContext.getBean(name, clazz);
    }
}