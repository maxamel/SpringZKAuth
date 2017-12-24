package com.github.maxamel.server.domain.model.constraints;

public interface DataUniqueConstraint {

    String getConstraintName();

    String[] getFieldNames();
}
