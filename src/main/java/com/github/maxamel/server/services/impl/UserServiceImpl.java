package com.github.maxamel.server.services.impl;

import com.github.maxamel.server.web.dtos.UserDto;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.ScheduleTaskService;
import com.github.maxamel.server.services.UserService;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Max Amelchenko
 */
@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper mapper;

    private final UserRepository repository;
    
    private final Map<Long, ScheduledExecutorService> kafkaTiming = new HashMap<>();
    
    @Value("${security.session.challengeFrequency}")
    private String chalFreq;
    
    @Value("${security.session.inactivityKickOut}")
    private String inactThreshold;
    
    @Value("${security.crypto.generator}")
    private String generator;
    
    @Value("${security.crypto.prime}")
    private String prime;
    
    @Autowired
    private ScheduleTaskService scheduler;
   
    @Autowired
    public UserServiceImpl(ModelMapper mapper, UserRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDto register(UserDto dto) {
        User user = mapper.map(dto, User.class);
        repository.findByName(user.getName()).ifPresent(u -> new DataIntegrityViolationException("User already exists with name: " + u.getName()));
        
        User newuser = repository.save(user);
        return mapper.map(newuser, UserDto.class);
    }

    @Override
    @Transactional
    public void remove(long id, String sessionId) {
        User user = repository.findOne(id).orElseThrow(() -> new EmptyResultDataAccessException("No user found with id: " + id, 1));
        if (verify(user,sessionId)) repository.delete(id);
        else
        {      
            throwChallengedException(user);
        }
    }

    @Override
    @Transactional
    public void removeByName(String name, String sessionId) {
        User user = repository.findByName(name).orElseThrow(() -> new EmptyResultDataAccessException("No user found with name: " + name, 1));
        if (user.getSecret()!=null && verify(user,sessionId)) repository.deleteByName(name);
        else 
        {
            throwChallengedException(user);
        }
    }

    @Override
    public UserDto fetch(String name, String sessionId) {        
        User user = repository.findByName(name).orElseThrow(() -> new EmptyResultDataAccessException("No user found with name: " + name, 1));
        
        if (user.getSecret()!=null && verify(user,sessionId)) 
        {
            return mapper.map(user, UserDto.class);
        }
        else 
        {
            throwChallengedException(user);
        }
        return null;
    }
    
    @Transactional
    public boolean verify(User user,String response)
    {
        BigInteger passwordless = new BigInteger(user.getPasswordless(),16); 
        BigInteger secret = new BigInteger(user.getSecret(), 16);
    
        BigInteger verify = passwordless.modPow(secret, new BigInteger(prime,16));
        
        if (verify.toString().equals(response)) 
        {
            SessionStatus status = user.getSstatus();
            if (!user.getSstatus().equals(SessionStatus.VALIDATED))
            {
                user.setSstatus(SessionStatus.VALIDATED);
                repository.save(user);
            }
            if (status.equals(SessionStatus.INITIATING)) scheduleAuthTask(user);
        }
        else 
        {
            user.setSstatus(SessionStatus.INVALIDATED);
            user.setSecret(null);
            repository.save(user);
            if (kafkaTiming.containsKey(user.getId())) 
            {
                ScheduledExecutorService  execService = kafkaTiming.get(user.getId());
                execService.shutdownNow();
            }
            return false;
        }
        return true;
    }
    
    private void throwChallengedException(User user) {
        if (user.getSecret() == null)
        {
            generateServerSecret(user);  
        }
        BigInteger power = new BigInteger(generator,16).modPow(new BigInteger(user.getSecret(),16), new BigInteger(prime,16)); 
        throw new AccessDeniedException(""+power);
    }

    @Transactional
    public void generateServerSecret(User olduser) {
        User user = repository.findByName(olduser.getName()).orElseThrow(() -> new EmptyResultDataAccessException("No user found with name: " + olduser.getName(), 1));
        SecureRandom random = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes(Charset.defaultCharset()));
        BigInteger bigint =  new BigInteger(256, random);
        user.setSecret(bigint.toString(16));
        user.setSstatus(SessionStatus.INITIATING);
        repository.save(user);
    }

    private void scheduleAuthTask(User user) 
    {   
        ScheduledExecutorService execService = Executors.newScheduledThreadPool(2);
        
        execService.scheduleAtFixedRate(new Runnable() {
       
            @Override
            public void run() {
                scheduler.publishChallenge(user);
            }
        }, 0, Long.parseLong(chalFreq), TimeUnit.MILLISECONDS);
        
        execService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                scheduler.handleActivity(user, kafkaTiming);
            }
        }, Long.parseLong(inactThreshold), Long.parseLong(inactThreshold), TimeUnit.MILLISECONDS);
        
        kafkaTiming.put(user.getId(), execService);
    }
    
}