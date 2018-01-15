package com.github.maxamel.server.web.dtos;

/**
 * @author Idan Rozenfeld
 */
public enum ErrorCodes {
    UNAUTHORIZED,
    NOT_FOUND,
    REQUEST_NOT_READABLE,
    UNKNOWN,
    MISSING_REQUEST_PARAM,
    DATA_VALIDATION,
    METHOD_NOT_ALLOWED,
    HTTP_MEDIA_TYPE_NOT_SUPPORTED
}
