package com.example.pay.configuration.activemq;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * activemq的参数配置
 */
@ConfigurationProperties(prefix = "spring.activemq")
public class ActivemqProperties {

    private String waizong;
    private String kuajing;
    private String factoryName;
    private String brokerUrl;
    private String user;
    private String password;

    private ActivemqProperties() {
    }

    @PostConstruct
    public void init() {
        System.out.println(waizong);
        System.out.println(kuajing);
        System.out.println(factoryName);
        System.out.println(brokerUrl);
        System.out.println(user);
        System.out.println(password);
    }

    public String getWaizong() {
        return waizong;
    }

    public void setWaizong(String waizong) {
        this.waizong = waizong;
    }

    public String getKuajing() {
        return kuajing;
    }

    public void setKuajing(String kuajing) {
        this.kuajing = kuajing;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
