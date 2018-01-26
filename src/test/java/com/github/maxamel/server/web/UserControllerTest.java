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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;

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
                .passwordless(new BigInteger("49663222554763"))
                .build();

        UserDto result = UserDto.builder()
                .id(1L)
                .name("John")
                .passwordless(new BigInteger("49663222554763"))
                .build();

        when(service.register(any(UserDto.class))).thenReturn(result);
        mvc.perform(post("/users")
                .content(writer.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(equalTo(1))))
                .andExpect(jsonPath("$.name", is(equalTo("John"))))
                .andExpect(jsonPath("$.passwordless", is(equalTo(new BigInteger("49663222554763").longValue()))));

        verify(service, times(1)).register(any(UserDto.class));
        verifyNoMoreInteractions(service);

    }

    @Test
    public void registerValidationFailedUniqueName() throws Exception {
        UserDto request = UserDto.builder()
                .name("John")
                .passwordless(new BigInteger("2324431211357"))
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
    public void catalogueValidationFailedWithEmptyName() throws Exception {
        UserDto request = UserDto.builder()
                .name("")
                .passwordless(new BigInteger("2324431211357"))
                .build();

        mvc.perform(post("/users")
                .content(writer.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is(equalTo(ErrorCodes.DATA_VALIDATION.toString()))))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName", is(equalTo("name"))))
                .andExpect(jsonPath("$.errors[0].errorCode", is(equalTo("NotEmpty"))))
                .andExpect(jsonPath("$.errors[0].rejectedValue", is(equalTo(""))))
                .andExpect(jsonPath("$.errors[0].params").isArray())
                .andExpect(jsonPath("$.errors[0].params").isEmpty());

        verifyNoMoreInteractions(service);
    }

  
}
