package com.github.maxamel.server.services;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.web.dtos.UserDto;

/**
 * @author Max Amelchenko
 */
public interface UserService {

    public UserDto register(UserDto dto);

    public void removeByName(String name, String sessionId);
    
    public UserDto fetch(String name, String sessionId) throws AccessDeniedException, EmptyResultDataAccessException;

    public void generateServerSecret(User user);
}