package com.github.maxamel.server.services.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.ScheduleTaskService;
import com.github.maxamel.server.web.dtos.ChallengeDto;

@Service
public class ScheduleTaskServiceImpl implements ScheduleTaskService{

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private KafkaProduceServiceImpl producer;
    
    @Value("${kafka.host.challengetopic}")
    private String topic;
    
    @Value("${security.crypto.generator}")
    private String generator;
    
    @Value("${security.crypto.prime}")
    private String prime;
    
    @Override
    @Transactional
    public void publishChallenge(User user)
    {
        Logger log = LoggerFactory.getLogger(ScheduleTaskService.class);
        log.info("Publishing message to kafka..." + user.getName());
        SecureRandom random = new SecureRandom();
        BigInteger bigint =  new BigInteger(256, random);
        user.setServerSecret(bigint.toString());
        repository.save(user);  

        BigInteger power = new BigInteger(generator,16).modPow(new BigInteger(user.getServerSecret(),16), new BigInteger(prime,16)); 
        ChallengeDto dto = new ChallengeDto(power.toString(16));
        producer.send(topic, dto);
    }
    
    @Override
    @Transactional
    public void handleActivity(User user, List<Timer> timers)
    {
        Timer challengeTimer = timers.get(0);
        Timer inactTimer = timers.get(1);
        if (user.getSessionStatus().equals(SessionStatus.WAITING)) 
        {
            Logger log = LoggerFactory.getLogger(ScheduleTaskService.class);
            log.info("Inactivity threshold reached! Invalidating..." + user.getName());
            challengeTimer.cancel();
            user.setSessionStatus(SessionStatus.INVALIDATED);
            user.setServerSecret(null);
            repository.save(user);
            inactTimer.cancel();
        }
        else 
        {
            user.setSessionStatus(SessionStatus.WAITING);
            repository.save(user); 
        }
    }
}
