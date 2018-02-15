package com.github.maxamel.server.web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.web.dtos.audit.AuditableDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Max Amelchenko
 */
@ApiModel("User")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = true)
public class UserDto extends AuditableDto {
    private static final long serialVersionUID = 5762617605382814204L;

    @ApiModelProperty(required = true)
    private Long id;

    @ApiModelProperty(required = true)
    @NotEmpty
    private String name;

    @ApiModelProperty(required = true)
    @NotNull
    private String passwordless;

    @ApiModelProperty(required = false)
    @JsonIgnore
    private String secret;
    
    @ApiModelProperty(required = false)
    @JsonIgnore
    private SessionStatus sstatus;
}