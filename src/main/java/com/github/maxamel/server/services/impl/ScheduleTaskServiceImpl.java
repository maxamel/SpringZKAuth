package com.github.maxamel.server.services.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.maxamel.server.domain.model.Constants;
import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.ScheduleTaskService;
import com.github.maxamel.server.web.dtos.ChallengeDto;

@Service
@Transactional
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
    public void publishChallenge(User olduser)
    {
        User user = repository.findByName(olduser.getName()).orElseThrow(() -> new EmptyResultDataAccessException("No user found with name: " + olduser.getName(), 1));
        Logger log = LoggerFactory.getLogger(ScheduleTaskService.class);
        log.info("Publishing message to kafka..." + user.getName());
        if (!user.getSstatus().equals(SessionStatus.INVALIDATED))
        {
            SecureRandom random = new SecureRandom();
            BigInteger bigint =  new BigInteger(256, random);
            user.setSecret(bigint.toString());
            repository.save(user);  
            BigInteger power = new BigInteger(generator,16).modPow(new BigInteger(user.getSecret(),16), new BigInteger(prime,16)); 
            ChallengeDto dto = new ChallengeDto(power.toString(16));
            producer.send(topic, dto);
        }
    }
    
    @Override
    public void handleActivity(User olduser, List<Timer> timers)
    {
        User user = repository.findByName(olduser.getName()).orElseThrow(() -> new EmptyResultDataAccessException("No user found with name: " + olduser.getName(), 1));
        Timer challengeTimer = timers.get(Constants.TIMER_CHALLENGE);
        Timer inactTimer = timers.get(Constants.TIMER_INACTIVITY);
        if (user.getSstatus().equals(SessionStatus.WAITING)) 
        {
            Logger log = LoggerFactory.getLogger(ScheduleTaskService.class);
            log.info("Inactivity threshold reached! Invalidating..." + user.getName());
            challengeTimer.cancel();
            user.setSstatus(SessionStatus.INVALIDATED);
            user.setSecret(null);
            repository.save(user);
            inactTimer.cancel();
        }
        else 
        {
            Logger log = LoggerFactory.getLogger(ScheduleTaskService.class);
            log.info("Detected activity! Resuming..." + user.getName());
            user.setSstatus(SessionStatus.WAITING);
            repository.save(user); 
        }
    }
}
