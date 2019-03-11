package com.example.pay.service.activemq.template;

import com.example.pay.configuration.activemq.ActivemqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * ActiveMQ队列消息消费者
 */
@Component
public class ActiveMQQueueConsumer {

    private final static Logger logger = LoggerFactory.getLogger(ActiveMQQueueConsumer.class);

    @Autowired
    private ActivemqProperties activemqProperties;

    @JmsListener(destination = "${spring.activemq.kuajing}", containerFactory = "${spring.activemq.factory.name}")
    public void receiptMessage(String message) {
        logger.info("消费了一条来自{}频道的消息：{}。",activemqProperties.getKuajing() , message);
    }


}
