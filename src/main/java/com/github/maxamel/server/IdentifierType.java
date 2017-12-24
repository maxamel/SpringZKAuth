package com.github.maxamel.server;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Idan Rozenfeld
 */
@FunctionalInterface
public interface IdentifierType<V> {
    @JsonValue
    V getValue();
}
