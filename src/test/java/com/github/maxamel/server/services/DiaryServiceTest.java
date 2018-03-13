package com.github.maxamel.server.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.maxamel.server.domain.model.Diary;
import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
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
    
    @Value("${test.entryname}")
    private String entryname;
    
    @Value("{test.username}")
    private String username;
    
    @Test
    public void removeByUsernameAndEntryname()
    {
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
    public void removeAll()
    {
        Diary result1 = Diary.builder()
                .id(1L)
                .username(username)
                .entryname(entryname+"1")
                .build();
        
        Diary result2 = Diary.builder()
                .id(2L)
                .username(username)
                .entryname(entryname+"2")
                .build();
        
        List<Diary> list = Arrays.asList(result1 , result2);
        when(repository.findByUsername(any(String.class))).thenReturn(list);
        
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                String toBeDeleted1 = (String) args[0];
                assertTrue(toBeDeleted1.equals(username));
                return null;
            }
        }).when(repository).deleteByUsernameAndEntryname(Matchers.any(String.class), Matchers.any(String.class));
        diaryService.removeAll(username, pass);
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
        DiaryDto dto = diaryService.add(result, pass);
        assertTrue(dto.getId().equals(result.getId()));
        assertTrue(dto.getUsername().equals(result.getUsername()));
        assertTrue(dto.getEntryname().equals(result.getEntryname()));
    }
    
    @Test
    public void fetch()
    {
        Diary result = Diary.builder()
                .id(1L)
                .username(username)
                .entryname(entryname)
                .build();
        Optional<Diary> opt = Optional.of(result);
        
        when(repository.findByUsernameAndEntryname(any(String.class), any(String.class))).thenReturn(opt);
        DiaryDto dto = diaryService.fetch(username, entryname, pass);
        assertTrue(dto.getId().equals(result.getId()));
        assertTrue(dto.getUsername().equals(result.getUsername()));
        assertTrue(dto.getEntryname().equals(result.getEntryname()));
    }
    
    @Test
    public void fetchByUsername()
    {
        Diary result1 = Diary.builder()
                .id(1L)
                .username(username)
                .entryname(entryname+"1")
                .build();
        
        Diary result2 = Diary.builder()
                .id(2L)
                .username(username)
                .entryname(entryname+"2")
                .build();
        
        List<Diary> list = Arrays.asList(result1 , result2);
        
        when(repository.findByUsername(any(String.class))).thenReturn(list);
        List<DiaryDto> dtos = diaryService.fetchByUsername(username, pass);
        assertTrue(dtos.get(0).getId().equals(result1.getId()));
        assertTrue(dtos.get(1).getId().equals(result2.getId()));
    }
}
