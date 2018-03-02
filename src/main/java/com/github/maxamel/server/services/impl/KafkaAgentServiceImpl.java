package com.github.maxamel.server.services.impl;

import java.util.HashSet;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.github.maxamel.server.services.KafkaAgentService;
import com.github.maxamel.server.web.dtos.ChallengeDto;
import com.github.rozidan.springboot.logger.Loggable;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZkUtils;


@Service
@ConditionalOnProperty(value = "kafka.enabled", havingValue = "true")
public class KafkaAgentServiceImpl implements KafkaAgentService{

    private KafkaTemplate<String, ChallengeDto> kafkaTemplate;

    private ZkUtils zkUtils;

    private final HashSet<String> openTopics = new HashSet<String>();

    public KafkaAgentServiceImpl(@Value("${kafka.zookeeper.url}") String zkAddress, KafkaTemplate<String, ChallengeDto> kafkaTemplate)
    {
    	ZkClient zkClient = new ZkClient(zkAddress,10000,10000);
        zkClient.setZkSerializer(getZkSerializer());
        boolean isSecureKafkaCluster = false;
        // ZkUtils for Kafka was used in Kafka 0.9.0.0 for the AdminUtils API
        this.zkUtils = new ZkUtils(zkClient, new ZkConnection(zkAddress), isSecureKafkaCluster);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Loggable
    @Override
    public void send(String topic, ChallengeDto chal) {
        //make sure all messages with the same id will be ordered in the same partition
        ListenableFuture<SendResult<String, ChallengeDto>> future = kafkaTemplate.send(topic, chal);
        // register a callback with the listener to receive the result of the send
        // asynchronously
        future.addCallback(new ListenableFutureCallback<SendResult<String, ChallengeDto>>() {

            @Override
            public void onSuccess(SendResult<String, ChallengeDto> result) {
                Logger log = LoggerFactory.getLogger(KafkaAgentServiceImpl.class);
                log.info("Published message to kafka...");
            }

            @Override
            public void onFailure(Throwable ex) {
                Logger log = LoggerFactory.getLogger(KafkaAgentServiceImpl.class);
                log.info("Failed publishing message to kafka..." + ex.getMessage());
            }
        });

    }
    @Loggable
    @Override
    public void openTopic(String topic)
    {
        if (openTopics.contains(topic)) return;
        // Add topic configuration here
        if (!AdminUtils.topicExists(zkUtils, topic))
            AdminUtils.createTopic(zkUtils, topic, 1, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);

        openTopics.add(topic);
    }
    @Loggable
    @Override
    public void closeTopic(String topic)
    {
        // Add topic configuration here
        AdminUtils.deleteTopic(zkUtils, topic);
        openTopics.remove(topic); 
    }

    private ZkSerializer getZkSerializer() {
        return new ZkSerializer() {
            @Override
            public byte[] serialize(Object o)
                    throws ZkMarshallingError
            {
                return ZKStringSerializer.serialize(o);
            }

            @Override
            public Object deserialize(byte[] bytes)
                    throws ZkMarshallingError
            {
                return ZKStringSerializer.deserialize(bytes);
            }
        };
    }
}