package com.github.maxamel.server.web.dtos.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Idan Rozenfeld
 */
@Getter
@ToString
@EqualsAndHashCode
@ApiModel("Audit")
public class AuditableDto implements Serializable {
    private static final long serialVersionUID = -159284889641683544L;

    @ApiModelProperty(readOnly = true)
    private String createdBy;

    @ApiModelProperty(readOnly = true)
    private String lastModifiedBy;

}