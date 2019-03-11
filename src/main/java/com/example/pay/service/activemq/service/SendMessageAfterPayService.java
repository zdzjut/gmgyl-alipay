package com.example.pay.service.activemq.service;

import com.example.pay.configuration.activemq.ActivemqProperties;
import com.example.pay.service.activemq.template.ActiveMQQueueProducer;
import com.example.pay.util.ResponseResult;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(ActivemqProperties.class)
public class SendMessageAfterPayService {

    @Autowired
    private ActiveMQQueueProducer activeMQQueueProducer;

    @Autowired
    private ActivemqProperties activemqProperties;

    public void sendToWaizong(ResponseResult responseResult) {
        String json = new Gson().toJson(responseResult);
        activeMQQueueProducer.sendMsg(activemqProperties.getWaizong(), json);
    }
    public void sendToKuajing(ResponseResult responseResult) {
        String json = new Gson().toJson(responseResult);
        activeMQQueueProducer.sendMsg(activemqProperties.getKuajing(), json);
    }


}