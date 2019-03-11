package com.example.pay.configuration.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * ActiveMQ消息队列配置类
 */
@Configuration
@EnableJms
@EnableConfigurationProperties(ActivemqProperties.class)
public class ActiveMQConfiguration {

    @Autowired
    private ActivemqProperties activemqProperties;

    /**
     * 动态配置bean的name属性
     */
    @ConfigurationProperties(prefix = "spring.activemq.factory")
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerQueue() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory());
        factory.setConcurrency("3-10");
        factory.setRecoveryInterval(1000L);
        return factory;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(activemqProperties.getBrokerUrl());
        activeMQConnectionFactory.setUserName(activemqProperties.getUser());
        activeMQConnectionFactory.setPassword(activemqProperties.getPassword());
        return activeMQConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsQueueTemplate() {
        return new JmsTemplate(activeMQConnectionFactory());
    }
}