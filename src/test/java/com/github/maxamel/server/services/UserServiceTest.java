package com.github.maxamel.server.services;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.UserService;
import com.github.maxamel.server.services.impl.KafkaProduceServiceImpl;
import com.github.maxamel.server.services.impl.UserServiceImpl;
import com.github.maxamel.server.services.mapping.MappingBasePackage;
import com.github.maxamel.server.web.dtos.UserDto;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WithModelMapper(basePackageClasses = MappingBasePackage.class)
@ContextConfiguration(classes = UserServiceImpl.class, initializers = ConfigFileApplicationContextInitializer.class)

public class UserServiceTest {

    @Autowired
    private UserService service;

    @MockBean
    private UserRepository repository;
    
    @MockBean
    private ScheduleTaskService scheduler;
    
    @Mock
    private KafkaProduceServiceImpl kafka;
    
    @Value("${test.passwordless}")
    private String pass;
    
    @Value("${test.secret}")
    private String sec;
    
    @Value("${test.wrongsecret}")
    private String wsec;
    
    @Value("${test.answer}")
    private String answer;

    @Test
    public void fetchWaitToValidate()
    {
        User result = User.builder()
                .id(1L)
                .name("John")
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        UserDto dto = service.fetch("John", answer);
        assertTrue(dto.getSstatus().equals(SessionStatus.VALIDATED));
    }
    
    @Test(expected = AccessDeniedException.class)
    public void fetchAuthenticationError()
    {
        User result = User.builder()
                .id(1L)
                .name("John")
                .passwordless(pass)
                .secret(wsec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        service.fetch("John", answer);
    }
    
    @Test(expected = AccessDeniedException.class)
    public void fetchNoSecret()
    {
        User result = User.builder()
                .id(1L)
                .name("John")
                .passwordless(pass)
                .secret(null)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        service.fetch("John", answer);
    }
    
    @Test
    public void verifyCorrect() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        User user = User.builder()
                .id(1L)
                .name("John")
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        
        Class<UserServiceImpl> c = UserServiceImpl.class;
        Class[] cArgs = new Class[2];
        cArgs[0] = User.class;
        cArgs[1] = String.class;
        Method method = c.getDeclaredMethod("verify", cArgs);
        method.setAccessible(true);
        Boolean obj = (Boolean) method.invoke(service, user, answer);
        assertTrue(user.getSstatus().equals(SessionStatus.VALIDATED));
        assertTrue(obj.booleanValue());
    }
    
    @Test
    public void verifyIncorrect() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        User user = User.builder()
                .id(1L)
                .name("John")
                .passwordless(pass)
                .secret(wsec)
                .sstatus(SessionStatus.WAITING)
                .build();
        
        Class<UserServiceImpl> c = UserServiceImpl.class;
        Class[] cArgs = new Class[2];
        cArgs[0] = User.class;
        cArgs[1] = String.class;
        Method method = c.getDeclaredMethod("verify", cArgs);
        method.setAccessible(true);
        Boolean obj = (Boolean) method.invoke(service, user, answer);
        assertTrue(user.getSstatus().equals(SessionStatus.INVALIDATED));
        assertTrue(user.getSecret() == null);
        assertTrue(!obj.booleanValue());
    }
}
