package com.github.maxamel.server.domain.model.types;

import com.github.maxamel.server.EnumUtils;
import com.github.maxamel.server.IdentifierType;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author Idan Rozenfeld
 */
public enum ProductCategory implements IdentifierType<Integer> {
    GAME(1), CLOTHING(2);

    private final int id;

    ProductCategory(int id) {
        this.id = id;
    }

    public static ProductCategory byValue(int value) {
        if (Objects.nonNull(value)) {
            return EnumUtils.getByValue(ProductCategory.class, value);
        }

        return null;
    }

    @Override
    public Integer getValue() {
        return id;
    }

    @Converter(autoApply = true)
    public static class ProductCategoryConverter implements AttributeConverter<ProductCategory, Integer> {

        @Override
        public Integer convertToDatabaseColumn(ProductCategory attribute) {
            if (Objects.nonNull(attribute)) {
                return attribute.getValue();
            }
            return null;
        }

        @Override
        public ProductCategory convertToEntityAttribute(Integer dbData) {
            if (Objects.nonNull(dbData)) {
                return ProductCategory.byValue(dbData);
            }
            return null;
        }

    }
}
