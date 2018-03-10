package com.github.maxamel.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SuppressWarnings("PMD")
@SpringBootApplication
public class ZKAuthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ZKAuthApplication.class, args);
    }

}
