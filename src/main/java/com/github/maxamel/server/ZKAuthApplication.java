package com.github.maxamel.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class ZKAuthApplication {
    
    private ZKAuthApplication(){}
    
    public static void main(String[] args) {
        SpringApplication.run(ZKAuthApplication.class, args);
    }

}
