package com.example.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class GmgylPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmgylPayApplication.class, args);
    }
}
