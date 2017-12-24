package com.github.maxamel.server.web.dtos.errors;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.springframework.data.auditing.CurrentDateTimeProvider;

/**
 * @author Idan Rozenfeld
 */
@ApiModel("Error")
@Getter
@Setter
@Builder
public class ErrorDto implements Serializable {
    private static final long serialVersionUID = -4708936233513887899L;

    private Enum<?> errorCode;

    private String message;

    @Builder.Default
    private Date timestamp = CurrentDateTimeProvider.INSTANCE.getNow().getTime();

    @Singular
    private Set<Object> errors;
}