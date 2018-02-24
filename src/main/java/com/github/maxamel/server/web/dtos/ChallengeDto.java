package com.github.maxamel.server.web.dtos;

import com.github.maxamel.server.web.dtos.audit.AuditableDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Max Amelchenko
 */
@ApiModel("Challenge")
@Getter
@AllArgsConstructor
@Builder
public class ChallengeDto extends AuditableDto {
    private static final long serialVersionUID = 5762617605382814204L;

    @ApiModelProperty(required = true)
    @NotNull
    private String challenge;

}