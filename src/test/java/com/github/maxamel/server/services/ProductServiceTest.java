package com.github.maxamel.server.services;

import com.github.maxamel.server.domain.model.Product;
import com.github.maxamel.server.domain.model.types.ProductCategory;
import com.github.maxamel.server.domain.repositories.ProductRepository;
import com.github.maxamel.server.services.impl.ProductServiceImpl;
import com.github.maxamel.server.services.ProductService;
import com.github.maxamel.server.services.mapping.MappingBasePackage;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WithModelMapper(basePackageClasses = MappingBasePackage.class)
@ContextConfiguration(classes = ProductServiceImpl.class)
public class ProductServiceTest {

    @Autowired
    private ProductService service;

    @MockBean
    private ProductRepository repository;

    @Test
    public void calculateAverage() {
        Product product = Product.builder()
                .name("John")
                .category(ProductCategory.GAME)
                .unitPrice(100F)
                .desc("desc")
                .build();
        Product product2 = Product.builder()
                .name("Mario")
                .category(ProductCategory.GAME)
                .unitPrice(51F)
                .desc("desc")
                .build();
        when(repository.findAll()).thenReturn(Arrays.asList(product, product2));
        float result = service.getProductPriceAvg();
        assertThat(result, is(equalTo(75.5F)));
    }

}
