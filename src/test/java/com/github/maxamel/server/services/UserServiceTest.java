package com.github.maxamel.server.services;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.impl.UserServiceImpl;
import com.github.maxamel.server.services.mapping.MappingBasePackage;
import com.github.maxamel.server.web.dtos.UserDto;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;

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
import org.springframework.dao.DataIntegrityViolationException;
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
    
    @MockBean
    private DiaryService diaryService;
    
    @MockBean
    private KafkaAgentService kafkaService;
    
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
    public void fetch()
    {
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        UserDto dto = service.fetch(username, answer);
        assertTrue(dto.getId().equals(result.getId()));
        assertTrue(dto.getName().equals(result.getName()));
        assertTrue(dto.getPasswordless().equals(result.getPasswordless()));
        assertTrue(dto.getSecret().equals(result.getSecret()));
        assertTrue(dto.getSstatus().equals(result.getSstatus()));
        assertTrue(dto.getSstatus().equals(SessionStatus.byValue(1)));
        assertTrue(dto.getSstatus().getValue().equals(1));
    }
    
    @Test
    public void register()
    {
    	User intermidiate = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        UserDto result = UserDto.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.empty();
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        when(repository.save(any(User.class))).thenReturn(intermidiate);
        UserDto dto = service.register(result);
        assertTrue(dto.getId().equals(result.getId()));
        assertTrue(dto.getName().equals(result.getName()));
        assertTrue(dto.getPasswordless().equals(result.getPasswordless()));
        assertTrue(dto.getSecret().equals(result.getSecret()));
        assertTrue(dto.getSstatus().equals(result.getSstatus()));
    }
    
    
    @Test
    public void removeByName()
    {
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        
        Mockito.doAnswer(new Answer<Object>() {
        	@Override
        	public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        		Object[] args = invocationOnMock.getArguments();
        		String toBeDeleted = (String) args[0];
        		assertTrue(toBeDeleted.equals(result.getName()));
        		return null;
        	}
        }).when(repository).deleteByName(Matchers.any(String.class));
        service.removeByName(result.getName(),answer);
    }
    
    @Test
    public void fetchWaitToValidate()
    {
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        UserDto dto = service.fetch(username, answer);
        assertTrue(dto.getSstatus().equals(SessionStatus.VALIDATED));
    }
    
    @Test(expected = AccessDeniedException.class)
    public void fetchAuthenticationError()
    {
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(wsec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        service.fetch(username, answer);
    }
    
    @Test(expected = AccessDeniedException.class)
    public void fetchNoSecret()
    {
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(null)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        service.fetch(username, answer);
    }
    
    @Test(expected = AccessDeniedException.class)
    public void removeNonExistentUser()
    {
        User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(wsec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        service.fetch(username, answer);
    }
    

    @Test(expected = DataIntegrityViolationException.class)
    public void findByNameExistingUser() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        
        when(repository.findByName(any(String.class))).thenThrow(new DataIntegrityViolationException("User already exists with name:" + user.getName()));
        service.register(user);
    }
    
    @Test
    public void verifyCorrect() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        User user = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.INITIATING)
                .build();
        
        Class<UserServiceImpl> c = UserServiceImpl.class;
        Class<?>[] cArgs = new Class[2];
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
                .name(username)
                .passwordless(pass)
                .secret(wsec)
                .sstatus(SessionStatus.WAITING)
                .build();
        
        Class<UserServiceImpl> c = UserServiceImpl.class;
        Class<?>[] cArgs = new Class[2];
        cArgs[0] = User.class;
        cArgs[1] = String.class;
        Method method = c.getDeclaredMethod("verify", cArgs);
        method.setAccessible(true);
        Boolean obj = (Boolean) method.invoke(service, user, answer);
        assertTrue(user.getSstatus().equals(SessionStatus.INVALIDATED));
        assertTrue(user.getSecret() == null);
        assertTrue(!obj.booleanValue());
    }
    
    @Test 
    public void generateServerSecret()
    {
    	User input = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
    	User result = User.builder()
                .id(1L)
                .name(username)
                .passwordless(pass)
                .secret(sec)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        
        Mockito.doAnswer(new Answer<Object>() {
        	@Override
        	public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        		Object[] args = invocationOnMock.getArguments();
        		User toBeUpdated = (User) args[0];
        		assertTrue(toBeUpdated.getName().equals(input.getName()));
        		assertTrue(!toBeUpdated.getSecret().equals(input.getSecret()));
        		assertTrue(toBeUpdated.getSstatus().equals(SessionStatus.INITIATING));
        		return null;
        	}
        }).when(repository).save(Matchers.any(User.class));
        service.generateServerSecret(input);
    }
}
