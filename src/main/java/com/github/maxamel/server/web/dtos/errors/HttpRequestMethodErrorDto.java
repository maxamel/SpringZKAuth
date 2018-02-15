package com.github.maxamel.server.web.dtos.errors;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.springframework.http.HttpMethod;

@ApiModel("HttpRequestMethodError")
@Getter
@Builder
public class HttpRequestMethodErrorDto implements Serializable {
    private static final long serialVersionUID = 4115067500106084449L;

    private String actualMethod;

    @Singular
    private List<HttpMethod> supportedMethods;
}
