package com.github.maxamel.server.config;

import com.github.maxamel.server.ZKAuthApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Idan Rozenfeld
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${application.formatted-version:development}")
    private String version;

    @Value("${application.title:Template}")
    private String title;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Template")
                .select()
                .apis(RequestHandlerSelectors.basePackage(ZKAuthApplication.class.getPackage().getName()))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .version(version)
                .build();

    }
}