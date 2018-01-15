package com.github.maxamel.server.services.impl;

import com.github.maxamel.server.web.dtos.UserDto;
import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.UserService;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${security.crypto.generator}")
    private String generator;
    
    @Value("${security.crypto.prime}")
    private String prime;
    
    @Value("${security.challenge.frequency}")
    private String frequency;
    
    @Value("${kafka.host.challengetopic}")
    private String topic;
    
    @Autowired
    private KafkaProducer producer;
   
    @Autowired
    public UserServiceImpl(ModelMapper mapper, UserRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDto register(UserDto dto) {
        User user = mapper.map(dto, User.class);
        repository.findByName(user.getName()).ifPresent(u -> new EmptyResultDataAccessException("User already exists with name: " + u.getName(), 1));
        
        User newuser = repository.save(user);
        return mapper.map(newuser, UserDto.class);
    }

    @Override
    @Transactional
    public void remove(long id, String sessionId) {
        User user = repository.findOne(id).orElseThrow(() -> new EmptyResultDataAccessException("No user found with id: " + id, 1));
        if (user.getSessionid().equals(sessionId)) repository.delete(id);
        else 
            throwChallengedException(user);
    }

    @Override
    @Transactional
    public void removeByName(String name, String sessionId) {
        User user = repository.findByName(name).orElseThrow(() -> new EmptyResultDataAccessException("No user found with name: " + name, 1));
        if (user.getSessionid().equals(sessionId)) repository.deleteByName(name);
        else throwChallengedException(user);
    }

    @Override
    @Transactional
    public UserDto example(String name, String sessionId) {        
        User user = repository.findByName(name).orElseThrow(() -> new EmptyResultDataAccessException("No user found with name: " + name, 1));
        if (user.getSessionid().equals(sessionId)) 
        {
            UserDto dto = mapper.map(user, UserDto.class);
            return dto;
        }
        else throwChallengedException(user);
        return null;
    }
    
    private void throwChallengedException(User user) {
        if (user.getChallenge() == null)
        {
            SecureRandom random = new SecureRandom();
            BigInteger bigint =  new BigInteger(256, random);
            user.setChallenge(bigint);
            repository.save(user);  
        }
        BigInteger power = new BigInteger(generator).modPow(user.getChallenge(), new BigInteger(prime)); 
        scheduleAuthTask(user);
        throw new AccessDeniedException(""+power);
    }

    private void scheduleAuthTask(User user) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            
            @Override
            public void run() {
                if (user.getSessionstatus().equals(SessionStatus.WAITING)) 
                {
                    user.setSessionstatus(SessionStatus.INVALIDATED);
                    user.setSessionid(null);
                    repository.save(user); 
                    timer.cancel();
                }
                else if (user.getSessionstatus().equals(SessionStatus.VALIDATED)) 
                {
                    SecureRandom random = new SecureRandom();
                    BigInteger bigint =  new BigInteger(256, random);
                    user.setChallenge(bigint);
                    user.setSessionstatus(SessionStatus.WAITING);
                    repository.save(user);  
                    UserDto dto = mapper.map(user, UserDto.class);
                    producer.send(topic, dto);
                }
            }
        }, Long.valueOf(frequency));
    }
    
}