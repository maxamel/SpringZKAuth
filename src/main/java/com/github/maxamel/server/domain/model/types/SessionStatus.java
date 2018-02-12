package com.github.maxamel.server.domain.model.types;

import com.github.maxamel.server.EnumUtils;
import com.github.maxamel.server.IdentifierType;
import java.util.Objects;

/**
 * @author Max Amelchenko
 */
public enum SessionStatus implements IdentifierType<Integer> {
    VALIDATED(1), WAITING(2), INVALIDATED(3), INITIATING(4);

    private final int id;

    SessionStatus(int id) {
        this.id = id;
    }

    public static SessionStatus byValue(int value) {
        if (Objects.nonNull(value)) {
            return EnumUtils.getByValue(SessionStatus.class, value);
        }

        return null;
    }

    @Override
    public Integer getValue() {
        return id;
    }
}
