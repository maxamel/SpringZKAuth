package com.github.maxamel.server.web.dtos;

import com.github.maxamel.server.web.dtos.audit.AuditableDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Max Amelchenko
 */
@ApiModel("Diary")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryDto extends AuditableDto {
    private static final long serialVersionUID = 5762617605382814204L;

    @ApiModelProperty(required = true)
    private Long id;

    @ApiModelProperty(required = true)
    @NotEmpty
    private String username;
    
    @ApiModelProperty(required = true)
    @NotEmpty
    private String entryname;

    @ApiModelProperty(required = true)
    @NotEmpty
    private String content;
}