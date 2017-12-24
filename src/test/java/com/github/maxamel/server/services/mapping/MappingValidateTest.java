package com.github.maxamel.server.services.mapping;

import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@WithModelMapper(basePackageClasses = MappingBasePackage.class)
public class MappingValidateTest {

    @Autowired
    private ModelMapper mapper;

    @Test
    public void mapperValidationShouldSuccess() {
        try {
            mapper.validate();

        } catch (ValidationException e) {
            fail(e.getMessage());
        }
    }
}
