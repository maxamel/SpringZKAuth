package com.github.maxamel.server.web.dtos.errors;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;


@ApiModel("Error")
@Getter
@Builder
public class ErrorDto implements Serializable {
    private static final long serialVersionUID = -4708936233513887899L;

    private Enum<?> errorCode;

    private String message;
    
    private String challenge;

    @Singular
    private Set<Object> errors;
}