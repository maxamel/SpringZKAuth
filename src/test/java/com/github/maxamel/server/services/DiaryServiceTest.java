package com.github.maxamel.server.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

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

import com.github.maxamel.server.domain.model.Diary;
import com.github.maxamel.server.domain.repositories.DiaryRepository;
import com.github.maxamel.server.services.impl.DiaryServiceImpl;
import com.github.maxamel.server.services.mapping.MappingBasePackage;
import com.github.maxamel.server.web.dtos.DiaryDto;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;


@RunWith(SpringRunner.class)
@WithModelMapper(basePackageClasses = MappingBasePackage.class)
@ContextConfiguration(classes = DiaryServiceImpl.class, initializers = ConfigFileApplicationContextInitializer.class)
public class DiaryServiceTest {

	@Autowired
    private DiaryService diaryService;
	
	@MockBean
    private UserService userService;
    
    @MockBean
    private DiaryRepository repository;
	
    @Value("${test.passwordless}")
    private String pass;
    
    @Value("${test.secret}")
    private String sec;
    
    @Value("${test.wrongsecret}")
    private String wsec;
    
    @Value("${test.entryname}")
    private String entryname;
    
    @Value("{test.username}")
    private String username;
    
    @Test
    public void removeByUsernameAndEntryname()
    {
        //when(userService.fetch(any(String.class), any(String.class))).thenReturn(new UserDto());

        Mockito.doAnswer(new Answer<Object>() {
        	@Override
        	public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        		Object[] args = invocationOnMock.getArguments();
        		String toBeDeleted1 = (String) args[0];
        		String toBeDeleted2 = (String) args[1];
        		assertTrue(toBeDeleted1.equals(username));
        		assertTrue(toBeDeleted2.equals(entryname));
        		return null;
        	}
        }).when(repository).deleteByUsernameAndEntryname(Matchers.any(String.class), Matchers.any(String.class));
        diaryService.removeByUsernameAndEntryname(username, entryname, pass);
    }
    
    @Test
    public void add()
    {
    	Diary intermidiate = Diary.builder()
                .id(1L)
                .username(username)
                .entryname(entryname)
                .build();
        DiaryDto result = DiaryDto.builder()
                .id(1L)
                .username(username)
                .entryname(entryname)
                .build();
        Optional<Diary> opt = Optional.empty();
        
        when(repository.findByUsernameAndEntryname(any(String.class), any(String.class))).thenReturn(opt);
        when(repository.save(any(Diary.class))).thenReturn(intermidiate);
        DiaryDto dto = diaryService.add(result, "sessionId");
        assertTrue(dto.getId().equals(result.getId()));
        assertTrue(dto.getUsername().equals(result.getUsername()));
        assertTrue(dto.getEntryname().equals(result.getEntryname()));
    }
}
