package com.example.pay.controller;

import com.example.pay.configuration.activemq.ActivemqProperties;
import com.example.pay.service.activemq.template.ActiveMQQueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
//@EnableScheduling
public class ActiveMQTest {

    @Autowired
    private ActiveMQQueueProducer activeMQQueueProducer;
    @Autowired
    private ActivemqProperties activemqProperties;

    @Scheduled(fixedRate = 10000, initialDelay = 3000)
    public void test() {
        activeMQQueueProducer.sendMsg(activemqProperties.getKuajing(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Date()));
    }

}