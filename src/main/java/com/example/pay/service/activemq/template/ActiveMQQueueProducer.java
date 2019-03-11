package com.example.pay.service.activemq.template;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * ActiveMQ消息生产者
 */
@Component
public class ActiveMQQueueProducer {
    
    private final static Logger logger = LoggerFactory.getLogger(ActiveMQQueueProducer.class);

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    
    public void sendMsg(String destinationName, String message) {
        logger.info("在{}频道生产了点对点消息：\"{}\"！", destinationName, message);
        Destination destination = new ActiveMQQueue(destinationName);
        jmsMessagingTemplate.convertAndSend(destination, message);
    }
    
}