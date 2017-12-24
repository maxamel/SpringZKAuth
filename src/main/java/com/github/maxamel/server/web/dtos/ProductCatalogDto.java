package com.github.maxamel.server.web.dtos;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Idan Rozenfeld
 */
@ApiModel("ProductCatalog")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class ProductCatalogDto implements Serializable {
    private static final long serialVersionUID = 2217746586939079984L;

    private String name;
    private int id;
}
