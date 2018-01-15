package com.github.maxamel.server.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.github.maxamel.server.web.dtos.UserDto;

@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, UserDto> kafkaTemplate;

    
    public void send(String topic, UserDto user) {
        //make sure all messages with the same id will be ordered in the same partition
        ListenableFuture<SendResult<String, UserDto>> future = kafkaTemplate.send(topic, user.getId().intValue() ,user);

        // register a callback with the listener to receive the result of the send
        // asynchronously
        future.addCallback(new ListenableFutureCallback<SendResult<String, UserDto>>() {

            @Override
            public void onSuccess(SendResult<String, UserDto> result) {
               
            }

            @Override
            public void onFailure(Throwable ex) {
                
            }
        });

    }
}