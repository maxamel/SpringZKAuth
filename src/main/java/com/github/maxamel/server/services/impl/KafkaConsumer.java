package com.github.maxamel.server.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.web.dtos.UserDto;

import java.math.BigInteger;
import java.util.Optional;


@Service
public class KafkaConsumer {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepo;

    @Value("${kafka.host.responsetopic}")
    private String kafkaTopic;
    
    @Value("${security.crypto.generator}")
    private String generator;
    
    @Value("${security.crypto.prime}")
    private String prime;

    @KafkaListener(containerFactory = "userKafkaListenerContainerFactory", topics = "${kafka.host.responsetopic}")
    public void handle(UserDto dto) {
        log.info("Handling new user from Kafka {} ", dto);
        Optional<User> optional = userRepo.findByName(dto.getName());
        if (optional.isPresent())
        {
            User user = optional.get();
            BigInteger password = new BigInteger(user.getPasswordless()); 
            BigInteger challenge = new BigInteger(user.getChallenge());
        
            BigInteger verify = password.modPow(challenge, new BigInteger(prime));
            if (!verify.equals(dto.getPasswordless())) 
            {
                user.setSessionid(null);
                user.setSessionstatus(SessionStatus.INVALIDATED);
            }
            else user.setSessionstatus(SessionStatus.VALIDATED);
            userRepo.save(user);
        }
    }
}
