package com.github.maxamel.server.web.dtos.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;

@Getter
@ApiModel("Audit")
public class AuditableDto implements Serializable {
    private static final long serialVersionUID = -159284889641683544L;

    @ApiModelProperty(readOnly = true)
    private String createdBy;

    @ApiModelProperty(readOnly = true)
    private String lastModifiedBy;

}