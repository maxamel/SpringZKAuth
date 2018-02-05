package com.github.maxamel.server.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.github.maxamel.server.web.dtos.ChallengeDto;
import com.github.rozidan.springboot.logger.Loggable;

@Component
public class KafkaProduceServiceImpl {

    @Autowired
    private KafkaTemplate<String, ChallengeDto> kafkaTemplate;

    @Loggable
    public void send(String topic, ChallengeDto chal) {
        //make sure all messages with the same id will be ordered in the same partition
        ListenableFuture<SendResult<String, ChallengeDto>> future = kafkaTemplate.send(topic, chal);
        // register a callback with the listener to receive the result of the send
        // asynchronously
        future.addCallback(new ListenableFutureCallback<SendResult<String, ChallengeDto>>() {

            @Override
            public void onSuccess(SendResult<String, ChallengeDto> result) {
                Logger log = LoggerFactory.getLogger(KafkaProduceServiceImpl.class);
                log.info("Published message to kafka...");
            }

            @Override
            public void onFailure(Throwable ex) {
                Logger log = LoggerFactory.getLogger(KafkaProduceServiceImpl.class);
                log.info("Failed publishing message to kafka...");
            }
        });

    }
}