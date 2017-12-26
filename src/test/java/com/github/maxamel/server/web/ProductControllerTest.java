package com.github.maxamel.server.web;

import com.github.maxamel.server.web.dtos.ErrorCodes;
import com.github.maxamel.server.web.dtos.ProductDto;
import com.github.maxamel.server.web.dtos.types.ProductCategoryDto;
import com.github.maxamel.server.config.JsonConfiguration;
import com.github.maxamel.server.domain.model.constraints.ProductNameUnique;
import com.github.maxamel.server.services.ProductService;
import com.github.maxamel.server.web.controllers.ProductController;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ComponentScan(basePackageClasses = ProductNameUnique.class)
@ContextConfiguration(classes = JsonConfiguration.class)
@WebMvcTest(secure = false, controllers = ProductController.class)
public class ProductControllerTest {

    @Autowired
    private ObjectWriter writer;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService service;

    @Test
    public void catalogueSuccess() throws Exception {
        ProductDto request = ProductDto.builder()
                .name("John")
                .category(ProductCategoryDto.CLOTHING)
                .unitPrice(100.0F)
                .build();

        ProductDto result = ProductDto.builder()
                .id(1L)
                .name("John")
                .category(ProductCategoryDto.CLOTHING)
                .unitPrice(100.0F)
                .build();

        when(service.catalogue(any(ProductDto.class))).thenReturn(result);
        mvc.perform(post("/products")
                .content(writer.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(equalTo(1))))
                .andExpect(jsonPath("$.name", is(equalTo("John"))))
                .andExpect(jsonPath("$.desc").doesNotExist())
                .andExpect(jsonPath("$.unitPrice", is(equalTo(100.0))))
                .andExpect(jsonPath("$.category", is(equalTo(ProductCategoryDto.CLOTHING.getValue()))));

        verify(service, times(1)).catalogue(any(ProductDto.class));
        verifyNoMoreInteractions(service);

    }

    @Test
    public void catalogueValidationFailedUniqueName() throws Exception {
        ProductDto request = ProductDto.builder()
                .name("John")
                .category(ProductCategoryDto.CLOTHING)
                .unitPrice(100.0F)
                .build();

        when(service.catalogue(any(ProductDto.class)))
                .thenThrow(new DataIntegrityViolationException("",
                        new ConstraintViolationException("", null, ProductNameUnique.CONSTRAINT_NAME)));
        mvc.perform(post("/products")
                .content(writer.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is(equalTo(ErrorCodes.DATA_VALIDATION.toString()))))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName", is(equalTo("name"))))
                .andExpect(jsonPath("$.errors[0].errorCode", is(equalTo("UNIQUE"))));

        verify(service, times(1)).catalogue(any(ProductDto.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void catalogueValidationFailedWithEmptyName() throws Exception {
        ProductDto request = ProductDto.builder()
                .name("")
                .category(ProductCategoryDto.CLOTHING)
                .unitPrice(100.0F)
                .build();

        mvc.perform(post("/products")
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

    @Test
    public void catalogueValidationFailedWithUnitPriceLowerThen10() throws Exception {
        ProductDto request = ProductDto.builder()
                .name("John")
                .category(ProductCategoryDto.CLOTHING)
                .unitPrice(8.5F)
                .build();

        mvc.perform(post("/products")
                .content(writer.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is(equalTo(ErrorCodes.DATA_VALIDATION.toString()))))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName", is(equalTo("unitPrice"))))
                .andExpect(jsonPath("$.errors[0].errorCode", is(equalTo("Min"))))
                .andExpect(jsonPath("$.errors[0].rejectedValue", is(equalTo(8.5))))
                .andExpect(jsonPath("$.errors[0].params").isArray())
                .andExpect(jsonPath("$.errors[0].params[0]", is(equalTo("10"))));
        
        verifyNoMoreInteractions(service);
    }
}
