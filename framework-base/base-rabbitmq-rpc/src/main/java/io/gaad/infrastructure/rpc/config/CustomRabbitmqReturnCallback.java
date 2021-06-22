package io.gaad.infrastructure.rpc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 当消息从exchage到queue投递失败的时候触发
 */
public class CustomRabbitmqReturnCallback implements RabbitTemplate.ReturnCallback {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        logger.error("message:{}", message);
        logger.error("replyCode:{}", replyCode);
        logger.error("replyText:{}", replyText);
        logger.error("exchange:{}", exchange);
        logger.error("routingKey:{}", routingKey);
    }
}
