package com.github.maxamel.server.web.dtos.errors;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@ApiModel("HttpMediaTypeError")
@Getter
@Setter
@Builder
public class HttpMediaTypeErrorDto implements Serializable {
    private static final long serialVersionUID = 7301072886218818L;

    private String mediaType;

}
