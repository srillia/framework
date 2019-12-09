package net.unsun.infrastructure.rpc.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.unsun.infrastructure.common.constant.BaseCode;
import net.unsun.infrastructure.common.kit.PageResultBean;
import net.unsun.infrastructure.common.kit.ResultBean;
import net.unsun.infrastructure.rpc.annotation.RpcClientMethod;
import net.unsun.infrastructure.rpc.entity.RpcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * RpcClientProxy
 *
 * @author toby
 */
public class RpcClientProxy implements InvocationHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(RpcClientProxy.class);

    private final Class<?> rpcClientInterface;
    private final String rpcName;
    private final RabbitTemplate syncSender;
    private final RabbitTemplate asyncSender;
    private final SimpleMessageListenerContainer messageListenerContainer;

    RpcClientProxy(Class<?> rpcClientInterface, String rpcName, RabbitTemplate syncSender, RabbitTemplate asyncSender, SimpleMessageListenerContainer messageListenerContainer) {
        this.rpcClientInterface = rpcClientInterface;
        this.rpcName = rpcName;
        this.syncSender = syncSender;
        this.asyncSender = asyncSender;
        this.messageListenerContainer = messageListenerContainer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取方法注解
        RpcClientMethod rpcClientMethod = method.getAnnotation(RpcClientMethod.class);
        if (rpcClientMethod == null) {
            return method.invoke(this, args);
        }
        RpcType methodRpcType = rpcClientMethod.type();
        // 未初始化完成
        if (methodRpcType == RpcType.SYNC && !this.messageListenerContainer.isRunning()) {
            return ResultBean.fail().setMessage("内部rpc，监听器没启动");
        }


        if (methodRpcType == RpcType.ASYNC && method.getGenericReturnType() != void.class) {
            throw new RuntimeException("ASYNC-RpcClient 返回类型只能为 void, Clas s: " + this.rpcClientInterface.getName() + ", Method: " + method.getName());
        }
        if (methodRpcType == RpcType.SYNC && (method.getGenericReturnType() != ResultBean.class && method.getGenericReturnType() != PageResultBean.class)) {
            throw new RuntimeException("SYNC-RpcClient 返回类型只能为 ResultBean 或者 PageResultBean, Class: " + this.rpcClientInterface.getName() + ", Method: " + method.getName());
        }
        String methodName = rpcClientMethod.value();
        if (StringUtils.isEmpty(methodName)) {
            methodName = method.getName();
        }
        // 组装data
        JSONObject data = new JSONObject();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            // 需要加上-parameters编译参数, 否则参数名不对
            String paramName = parameters[i].getName();
//            Parameter parameter = parameters[i];
//            Param param = parameter.getAnnotation(Param.class);
//            if (param != null && StringUtils.isNotBlank(param.value())) {
//                paramName = param.value();
//            } else {
//                LOGGER.warn(this.rpcType.getName() + "-RpcClient-" + this.rpcName + ", Method: " + methodName + ", 未加@Param或@Param的值为空");
//            }
            data.put(String.valueOf(i), args[i]);
        }
        // 调用参数
        JSONObject paramData = new JSONObject();
        paramData.put("command", methodName);
        paramData.put("data", data);
        String paramDataJsonString = paramData.toJSONString();
        try {
            if (methodRpcType == RpcType.ASYNC) {
                asyncSender.convertAndSend(paramDataJsonString);
                LOGGER.debug(methodRpcType.getName() + "-RpcClient-" + this.rpcName + ", Method: " + methodName + " Call Success, Param: " + paramDataJsonString);
                return null;
            }
            // 发起请求并返回结果
            long start = System.currentTimeMillis();
            Object resultJsonStr = syncSender.convertSendAndReceive(paramDataJsonString);
            if (resultJsonStr == null) {
                // 无返回任何结果，说明服务器负载过高，没有及时处理请求，导致超时
                LOGGER.error("Duration: " + (System.currentTimeMillis() - start) + "ms, " + methodRpcType.getName() + "-RpcClient-" + this.rpcName + ", Method: " + methodName + " Service Unavailable, Param: " + paramDataJsonString);
                return ResultBean.fail().setMessage("rpc 超时");
            }
            // 获取调用结果的状态
            JSONObject resultJson = JSONObject.parseObject(resultJsonStr.toString());
            //int status = resultJson.getIntValue("status");
            Object _data = resultJson.get("_data");
            Object _class = resultJson.get("_class");

            if(_data != null && _class != null && _class.toString().equals(ResultBean.class.getName())) {
                return JSON.parseObject(_data.toString(), ResultBean.class);
            }
            if(_data != null && _class != null && _class.toString().equals(PageResultBean.class.getName())) {
                return JSON.parseObject(_data.toString(), PageResultBean.class);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if(method.getReturnType() == ResultBean.class) {
            return ResultBean.fail().setMessage("rpc,没有返回值");
        }
        if(method.getReturnType() == PageResultBean.class){
            PageResultBean pageResultBean = PageResultBean.create();
            pageResultBean.setCode(BaseCode.fail);
            pageResultBean.setMessage("pc,没有返回值");
            return pageResultBean;
        }

        if(method.getReturnType() == void.class){
          return null;
        } else {
            return null;
        }
    }
}
