package com.github.maxamel.server.domain.model.constraints;

import org.springframework.stereotype.Component;

@Component
public class UserEntryNameUnique implements DataUniqueConstraint {

    public static final String CONSTRAINT_NAME = "UNIQUE_USERENTRY_NAME";
    public static final String USER_NAME = "username";
    public static final String ENTRY_NAME = "entryname";

    @Override
    public String getConstraintName() {
        return CONSTRAINT_NAME;
    }

    @Override
    public String[] getFieldNames() {
        return new String[]{USER_NAME, ENTRY_NAME};
    }
}