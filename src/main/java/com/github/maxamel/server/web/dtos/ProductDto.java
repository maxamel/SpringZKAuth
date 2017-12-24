package com.github.maxamel.server.web.dtos;

import com.github.maxamel.server.web.dtos.audit.AuditableDto;
import com.github.maxamel.server.web.dtos.types.ProductCategoryDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Set;
import javax.validation.constraints.Min;
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
 * @author Idan Rozenfeld
 */
@ApiModel("Product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = true)
public class ProductDto extends AuditableDto {
    private static final long serialVersionUID = 5762617605382814204L;

    @ApiModelProperty(allowEmptyValue = true)
    private Long id;

    @ApiModelProperty(required = true)
    @NotEmpty
    private String name;

    @ApiModelProperty(allowEmptyValue = true)
    @JsonProperty("desc")
    private String description;

    @ApiModelProperty(required = true, allowableValues = "range[10,infinity]")
    @Min(10)
    @NotNull
    private Float unitPrice;

    @ApiModelProperty(required = true)
    @NotNull
    private ProductCategoryDto category;

    private Set<ProductCatalogDto> catalogs;
}