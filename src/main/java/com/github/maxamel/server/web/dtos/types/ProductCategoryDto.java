package com.github.maxamel.server.web.dtos.types;

import com.github.maxamel.server.EnumUtils;
import com.github.maxamel.server.IdentifierType;
import java.util.Objects;

/**
 * @author Idan Rozenfeld
 */
public enum ProductCategoryDto implements IdentifierType<String> {
    GAME("G"), CLOTHING("C");

    private final String id;

    ProductCategoryDto(String id) {
        this.id = id;
    }

    public static ProductCategoryDto byValue(String value) {
        if (Objects.nonNull(value)) {
            return EnumUtils.getByValue(ProductCategoryDto.class, value);
        }

        return null;
    }

    @Override
    public String getValue() {
        return id;
    }
}
