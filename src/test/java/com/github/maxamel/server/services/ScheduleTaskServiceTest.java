package com.github.maxamel.server.services;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.impl.ScheduleTaskServiceImpl;
import com.github.maxamel.server.services.mapping.MappingBasePackage;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RunWith(SpringRunner.class)
@WithModelMapper(basePackageClasses = MappingBasePackage.class)
@ContextConfiguration(classes = ScheduleTaskServiceImpl.class, initializers = ConfigFileApplicationContextInitializer.class)

public class ScheduleTaskServiceTest {

	@MockBean
    private UserService service;

    @MockBean
    private UserRepository repository;
    
    @MockBean
    private KafkaAgentService kafka;
    
    @Autowired
    private ScheduleTaskService scheduler;
    
    
    @Value("${test.passwordless}")
    private String pass;
    
    @Value("${test.secret}")
    private String sec;
    
    @Value("${test.wrongsecret}")
    private String wsec;
    
    @Value("${test.answer}")
    private String answer;
    
    @Value("{test.username}")
    private String username;

    @Test
    public void publishChallengeValidatedUser()
    {
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.VALIDATED)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);

        Mockito.doAnswer(new Answer<Object>() {
        	@Override
        	public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        		Object[] args = invocationOnMock.getArguments();
        		User toBeSaved = (User) args[0];
        		System.out.println(toBeSaved.getSecret().length());
        		Assert.assertTrue(!toBeSaved.getSecret().equals(sec));
        		return null;
        	}
        }).when(repository).save(Matchers.any(User.class));
        scheduler.publishChallenge(result);
    }
    
    @Test
    public void handleActivityWaitingUser()
    {
        Map<Long, ScheduledExecutorService> timers = new HashMap<>();
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        ScheduledExecutorService execService = Executors.newScheduledThreadPool(2);
        timers.put(result.getId(), execService);
        when(repository.findByName(any(String.class))).thenReturn(opt);

        Mockito.doAnswer(new Answer<Object>() {
        	@Override
        	public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        		Object[] args = invocationOnMock.getArguments();
        		User toBeSaved = (User) args[0];
        		Assert.assertTrue(toBeSaved.getSecret() == null);
        		Assert.assertTrue(toBeSaved.getSstatus().equals(SessionStatus.INVALIDATED));
        		return null;
        	}
        }).when(repository).save(Matchers.any(User.class));
        scheduler.handleActivity(result, timers);

    }
    
    @Test
    public void handleActivityInitiatingUser()
    {
        Map<Long, ScheduledExecutorService> timers = new HashMap<>();
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.INITIATING)
                .build();
        Optional<User> opt = Optional.of(result);
        ScheduledExecutorService execService = Executors.newScheduledThreadPool(2);
        timers.put(result.getId(), execService);
        when(repository.findByName(any(String.class))).thenReturn(opt);

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                User toBeSaved = (User) args[0];
                Assert.assertTrue(toBeSaved.getSecret() == null);
                Assert.assertTrue(toBeSaved.getSstatus().equals(SessionStatus.INVALIDATED));
                return null;
            }
        }).when(repository).save(Matchers.any(User.class));
        scheduler.handleActivity(result, timers);

    }
    
    @Test
    public void handleActivityValidatedUser()
    {
        Map<Long, ScheduledExecutorService> timers = new HashMap<>();
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.VALIDATED)
                .build();
        Optional<User> opt = Optional.of(result);
        ScheduledExecutorService execService = Executors.newScheduledThreadPool(2);
        timers.put(result.getId(), execService);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                User toBeSaved = (User) args[0];
                Assert.assertTrue(toBeSaved.getSecret().equals(sec));
                Assert.assertTrue(toBeSaved.getSstatus().equals(SessionStatus.WAITING));
                return null;
            }
        }).when(repository).save(Matchers.any(User.class));
        scheduler.handleActivity(result, timers);

    }
    
}
