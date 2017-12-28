package com.github.maxamel.server.domain.model.constraints;

import org.springframework.stereotype.Component;

@Component
public class UserNameUnique implements DataUniqueConstraint {

    public static final String CONSTRAINT_NAME = "UNIQUE_USER_NAME";
    public static final String FIELD_NAME = "name";

    @Override
    public String getConstraintName() {
        return CONSTRAINT_NAME;
    }

    @Override
    public String[] getFieldNames() {
        return new String[]{FIELD_NAME};
    }
}