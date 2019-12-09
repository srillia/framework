package net.unsun.infrastructure.rpc.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import net.unsun.infrastructure.common.kit.PageResultBean;
import net.unsun.infrastructure.common.kit.ResultBean;
import net.unsun.infrastructure.rpc.annotation.RpcServerMethod;
import net.unsun.infrastructure.rpc.entity.RpcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RpcServerHandler
 *
 * @author toby
 */
public class RpcServerHandler implements ChannelAwareMessageListener, InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    private final static Map<String, FastMethod> FAST_METHOD_MAP = new ConcurrentHashMap<>();

    @Value("${spring.rabbitmq.slow-call-time:1000}")
    private int slowCallTime;

    private final Object rpcServerBean;
    private final String rpcName;
    private final RpcType rpcType;

    RpcServerHandler(Object rpcServerBean, String rpcName, RpcType rpcType) {
        this.rpcServerBean = rpcServerBean;
        this.rpcName = rpcName;
        this.rpcType = rpcType;
    }

    @Override
    public void afterPropertiesSet() {
        // 初始化所有接口
        Class<?> rpcServerClass = this.rpcServerBean.getClass();
        for (Method targetMethod : rpcServerClass.getMethods()) {
            if (targetMethod != null && targetMethod.isAnnotationPresent(RpcServerMethod.class)) {
                String methodName = targetMethod.getAnnotation(RpcServerMethod.class).value();
                if (StringUtils.isEmpty(methodName)) {
                    methodName = targetMethod.getName();
                }
                String key = this.rpcType.getName() + "_" + this.rpcName + "_" + methodName;
                if (FAST_METHOD_MAP.containsKey(key)) {
                    throw new RuntimeException("Class: " + rpcServerClass.getName() + ", Method: " + methodName + " 重复");
                }

                FastMethod fastMethod = FastClass.create(rpcServerClass).getMethod(targetMethod.getName(), targetMethod.getParameterTypes());
                if (fastMethod == null) {
                    throw new RuntimeException("Class: " + rpcServerClass.getName() + ", Method: " + targetMethod.getName() + " Invoke Exception");
                }

                if (fastMethod.getReturnType() != ResultBean.class && fastMethod.getReturnType() != PageResultBean.class && fastMethod.getReturnType() != void.class) {
                    throw new RuntimeException("返回类型只能为 ResultBean, PageResultBean 或者 void, Class: " + rpcServerClass.getName() + ", Method: " + fastMethod.getName() + ", ReturnType: " + fastMethod.getReturnType());
                }
                FAST_METHOD_MAP.put(key, fastMethod);
                LOGGER.debug(this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Method: " + methodName + " 已启动");
            }
        }
        LOGGER.info(this.rpcType.getName() + "-RpcServerHandler-" + this.rpcName + " 已启动");
    }

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        MessageProperties messageProperties = null;
        String messageStr = null;
        String jsonStrResult =null;
        try {
            messageProperties = message.getMessageProperties();
            messageStr = new String(message.getBody(), StandardCharsets.UTF_8);
            // 构建返回JSON值
            JSONObject resultJson = new JSONObject();
            try {
                // 组装参数json
                JSONObject paramData = JSON.parseObject(messageStr);
                // 获得当前command
                String command = paramData.getString("command");
                if (StringUtils.isEmpty(command)) {
                    LOGGER.error("Method Invoke Exception: Command 参数为空, " + this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Received: " + messageStr);
                    return;
                }
                // 获取data数据
                JSONObject data = paramData.getJSONObject("data");
                if (data == null) {
                    LOGGER.error("Method Invoke Exception: Data 参数错误, " + this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Method: " + command + ", Received: " + messageStr);
                    return;
                }
                // 异步执行任务
                if (RpcType.ASYNC == this.rpcType) {
                    long start = System.currentTimeMillis();
                    asyncExecute(command, data);
                    double offset = System.currentTimeMillis() - start;
                    LOGGER.info("Duration: " + offset + "ms, " + this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Method: " + command + ", Received: " + messageStr);
                    if (offset > this.slowCallTime) {
                        LOGGER.warn("Duration: " + offset + "ms, " + this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Method: " + command + ", Slower Called, Received: " + messageStr);
                    }
                    return;
                }
                // 同步执行任务并返回结果
                long start = System.currentTimeMillis();
                Object result = syncExecute(command, data);
                if (result != null) {
                    double offset = System.currentTimeMillis() - start;
                    LOGGER.info("Duration: " + offset + "ms, " + this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Method: " + command + ", Received: " + messageStr);
                    if (offset > this.slowCallTime) {
                        LOGGER.warn("Duration: " + offset + "ms, " + this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Method: " + command + ", Call Slowing");
                    }
                    resultJson.put("_data",result);
                    if(result.getClass().getName() == ResultBean.class.getName()) {
                        resultJson.put("_class",ResultBean.class.getName());
                    }
                    if(result.getClass().getName() == PageResultBean.class.getName()) {
                        resultJson.put("_class",PageResultBean.class.getName());
                    }
                }

            } catch (InvocationTargetException e) {
                // 获取目标异常
                Throwable t = e.getTargetException();
                LOGGER.error("Method Invoke Target Exception! Received: " + messageStr);
                LOGGER.error(t.getMessage(), t);
            } catch (Exception e) {
                LOGGER.error("Method Invoke Exception! Received: " + messageStr);
                LOGGER.error(e.getMessage(), e);
            }
            // 构建配置
            BasicProperties replyProps = new BasicProperties.Builder().correlationId(messageProperties.getCorrelationId()).contentEncoding(StandardCharsets.UTF_8.name()).contentType(messageProperties.getContentType()).build();
            // 反馈消息
            channel.basicPublish(messageProperties.getReplyToAddress().getExchangeName(), messageProperties.getReplyToAddress().getRoutingKey(), replyProps, resultJson.toJSONString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error(this.rpcType.getName() + "-RpcServer-" + this.rpcName + " Exception! Received: " + messageStr);
            LOGGER.error(e.getMessage(), e);
        } finally {
            // 确认处理任务
            if (messageProperties != null) {
                channel.basicAck(messageProperties.getDeliveryTag(), false);
            }
        }
    }

    /**
     * 同步调用
     */
    private void asyncExecute(String command, JSONObject data) throws InvocationTargetException {
        // 获取当前服务的反射方法调用
        String key = this.rpcType.getName() + "_" + this.rpcName + "_" + command;
        // 通过缓存来优化性能
        FastMethod fastMethod = FAST_METHOD_MAP.get(key);
        if (fastMethod == null) {
            LOGGER.error(this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Method: " + command + " Not Found");
            return;
        }
        List args = convertParamsTypes(command, data);


        // 通过发射来调用方法
        fastMethod.invoke(this.rpcServerBean, args.toArray());
    }

    private List convertParamsTypes(String command, JSONObject data) {
        List args = new ArrayList();
        for (Method targetMethod : this.rpcServerBean.getClass().getMethods()) {
            if (targetMethod.getName().equals(command)) {
                Parameter[] parameters = targetMethod.getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    String json = data.getString(String.valueOf(j));
                    Class _class = parameters[j].getType();
                    if(!StringUtils.isEmpty(json)) {
                        if (Collection.class.isAssignableFrom(_class)) {
                            args.add(Arrays.asList(JSONArray.parseArray(json).toArray()));
                        } else if (_class.isEnum()) {
                            //to do
                        } else if (_class.isArray()) {
                            args.add(JSONArray.parseArray(json).toArray());
                        } else if (CharSequence.class.isAssignableFrom(_class)) {
                            args.add(json);
                        } else if (Map.class.isAssignableFrom(_class)) {
                            args.add(JSON.parseObject(json, Map.class));
                        } else {
                            args.add(JSON.toJavaObject(JSON.parseObject(json), _class));
                        }
                    } else {
                        args.add(json);
                    }
                }
            }
        }
        return args;
    }

    /**
     * 异步调用
     */
    private Object syncExecute(String command, JSONObject data) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        // 获取当前服务的反射方法调用
        String key = this.rpcType.getName() + "_" + this.rpcName + "_" + command;
        // 通过缓存来优化性能
        FastMethod fastMethod = FAST_METHOD_MAP.get(key);
        if (fastMethod == null) {
            LOGGER.error(this.rpcType.getName() + "-RpcServer-" + this.rpcName + ", Method: " + command + " Not Found");
            return null;
        }
        List args = convertParamsTypes(command, data);

        // 通过反射来调用方法
        return fastMethod.invoke(this.rpcServerBean, args.toArray());
    }


}
