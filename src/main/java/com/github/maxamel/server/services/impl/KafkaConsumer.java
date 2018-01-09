package com.github.maxamel.server.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.web.dtos.UserDto;

import java.util.Optional;


@Service
public class KafkaConsumer {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepo;

    @Value("${kafka.host.topic}")
    private String kafkaTopic;
    
    @Value("${security.crypto.generator}")
    private String generator;
    
    @Value("${security.crypto.prime}")
    private String prime;

    @KafkaListener(containerFactory = "userKafkaListenerContainerFactory", topics = "${kafka.host.topic}")
    public void handle(UserDto dto) {
        log.info("Handling new user from Kafka {} ", dto);
        Optional<User> user = userRepo.findByName(dto.getName());
        if (user.isPresent())
        {
            //BigInteger partial_key = state.getPartial_key().modPow(state.getSecret(), g.getPrime());
        }
    }
    

}
