package com.github.maxamel.server.web.handlers;

import com.github.maxamel.server.web.dtos.ErrorCodes;
import com.github.maxamel.server.web.dtos.errors.ErrorDto;
import com.github.rozidan.springboot.logger.Loggable;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Max Amelchenko
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ValidationErrorHandlers {

    @Loggable
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorDto handleNotReadableError(HttpMessageNotReadableException ex) {
        return ErrorDto.builder()
                .errorCode(ErrorCodes.REQUEST_NOT_READABLE)
                .message(ex.getLocalizedMessage())
                .build();
    }
    
    @Loggable
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorDto handleUnauthorizedRequest(AccessDeniedException ex) {
        return ErrorDto.builder()
                .errorCode(ErrorCodes.UNAUTHORIZED)
                .challenge(ex.getMessage())
                .message("Unauthorized")
                .build();
    }
}