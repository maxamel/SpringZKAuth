package com.github.maxamel.server.web;

import com.github.maxamel.server.web.dtos.ErrorCodes;
import com.github.maxamel.server.web.dtos.UserDto;
import com.github.maxamel.server.config.JsonConfiguration;
import com.github.maxamel.server.domain.model.constraints.UserNameUnique;
import com.github.maxamel.server.services.UserService;
import com.github.maxamel.server.web.controllers.UserController;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ComponentScan(basePackageClasses = UserNameUnique.class)
@ContextConfiguration(classes = JsonConfiguration.class)
@WebMvcTest(secure = false, controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectWriter writer;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

    @Test
    public void registerSuccess() throws Exception {
        UserDto request = UserDto.builder()
                .id(1L)
                .name("John")
                .passwordless("49663222554763")
                .build();

        UserDto result = UserDto.builder()
                .id(1L)
                .name("John")
                .passwordless("49663222554763")
                .build();

        when(service.register(any(UserDto.class))).thenReturn(result);
        mvc.perform(post("/users")
                .content(writer.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(equalTo(1))))
                .andExpect(jsonPath("$.name", is(equalTo("John"))))
                .andExpect(jsonPath("$.passwordless", is(equalTo("49663222554763"))));

        verify(service, times(1)).register(any(UserDto.class));
        verifyNoMoreInteractions(service);

    }

    @Test
    public void registerValidationFailedUniqueName() throws Exception {
        UserDto request = UserDto.builder()
                .id(1L)
                .name("John")
                .passwordless("2324431211357")
                .build();

        when(service.register(any(UserDto.class)))
                .thenThrow(new DataIntegrityViolationException("",
                        new ConstraintViolationException("", null, UserNameUnique.CONSTRAINT_NAME)));
        mvc.perform(post("/users")
                .content(writer.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is(equalTo(ErrorCodes.DATA_VALIDATION.toString()))))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName", is(equalTo("name"))))
                .andExpect(jsonPath("$.errors[0].errorCode", is(equalTo("UNIQUE"))));

        verify(service, times(1)).register(any(UserDto.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void fetchSuccess() throws Exception {
        UserDto result = UserDto.builder()
                .id(1L)
                .name("Mike")
                .passwordless("2324431211357")
                .build();
        
        when(service.fetch(any(String.class), any(String.class))).thenReturn(result);
        mvc.perform(get("/users/Mike")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(equalTo(1))))
                .andExpect(jsonPath("$.name", is(equalTo("Mike"))))
                .andExpect(jsonPath("$.passwordless", is(equalTo("2324431211357"))));

        verify(service, times(1)).fetch(any(String.class), any(String.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void fetchNotFound() throws Exception {
        when(service.fetch(any(String.class), any(String.class)))
        .thenThrow(new EmptyResultDataAccessException("Not found",0));
        mvc.perform(get("/users/Mike")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is(equalTo(ErrorCodes.NOT_FOUND.toString()))))
                .andExpect(jsonPath("$.message", is(equalTo("Not found"))));
        
        verify(service, times(1)).fetch(any(String.class), any(String.class));
        verifyNoMoreInteractions(service);
    }
    
    @Test
    public void fetchWrongSessionId() throws Exception {
        when(service.fetch(any(String.class), any(String.class)))
        .thenThrow(new AccessDeniedException("134FF26B81CD285"));
        mvc.perform(get("/users/Mike")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode", is(equalTo(ErrorCodes.UNAUTHORIZED.toString()))))
                .andExpect(jsonPath("$.challenge", is(equalTo("134FF26B81CD285"))))
                .andExpect(jsonPath("$.message", is(equalTo("Unauthorized"))));
        
        verify(service, times(1)).fetch(any(String.class), any(String.class));
        verifyNoMoreInteractions(service);
    }
    
    @Test
    public void removeSuccess() throws Exception {
        doNothing().when(service).removeByName(any(String.class), any(String.class));
        mvc.perform(delete("/users/Mike")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).removeByName(any(String.class), any(String.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void removeNotFound() throws Exception {
        doThrow(new EmptyResultDataAccessException("Not found",0)).when(service).removeByName(any(String.class), any(String.class));
        mvc.perform(delete("/users/Mike")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is(equalTo(ErrorCodes.NOT_FOUND.toString()))))
                .andExpect(jsonPath("$.message", is(equalTo("Not found"))));
        
        verify(service, times(1)).removeByName(any(String.class), any(String.class));
        verifyNoMoreInteractions(service);
    }
    
    @Test
    public void removeWrongSessionId() throws Exception {
        doThrow(new AccessDeniedException("134FF26B81CD285")).when(service).removeByName(any(String.class), any(String.class));
        mvc.perform(delete("/users/Mike")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode", is(equalTo(ErrorCodes.UNAUTHORIZED.toString()))))
                .andExpect(jsonPath("$.challenge", is(equalTo("134FF26B81CD285"))))
                .andExpect(jsonPath("$.message", is(equalTo("Unauthorized"))));
        
        verify(service, times(1)).removeByName(any(String.class), any(String.class));
        verifyNoMoreInteractions(service);
    }
}
