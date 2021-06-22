package io.gaad.infrastructure.rpc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 生产者发送消息到exchange的处理结果
 * ack为true表示成功  否则表示消息未发送到exchange
 */
public class CustomRabbitmqConfirmCallback implements RabbitTemplate.ConfirmCallback {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //成功就不打印消息了
        logger.info("确认结果,{}", ack);
        if (!ack) {
            logger.error("消息唯一:{}", correlationData);  //correlationData对象里的ID为传递的参数字段
//            logger.error("确认结果:{}", ack);
            logger.error("失败原因:{}", cause);
        }
    }
}
