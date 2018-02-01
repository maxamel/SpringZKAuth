package com.github.maxamel.server.services;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.UserService;
import com.github.maxamel.server.services.impl.KafkaProduceServiceImpl;
import com.github.maxamel.server.services.impl.UserServiceImpl;
import com.github.maxamel.server.services.mapping.MappingBasePackage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

@RunWith(SpringRunner.class)
@ComponentScan(basePackageClasses = MappingBasePackage.class)
@ContextConfiguration(classes = UserServiceImpl.class)
public class UserServiceTest {

    @Autowired
    private UserService service;

    @MockBean
    private UserRepository repository;
    
    @Mock
    private KafkaProduceServiceImpl kafka;

    @Test
    public void fetchChangeStatus()
    {
        User result = User.builder()
                .id(1L)
                .name("John")
                .passwordless("49663222554763")
                .serverSecret("144DAC8064A")
                .sessionStatus(SessionStatus.VALIDATED)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        service.fetch("John", "sessionsId");
    }
    
}
