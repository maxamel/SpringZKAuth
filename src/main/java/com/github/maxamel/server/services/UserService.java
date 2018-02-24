package com.github.maxamel.server.services;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.web.dtos.UserDto;

/**
 * @author Max Amelchenko
 */
public interface UserService {

    UserDto register(UserDto dto);

    void removeByName(String name, String sessionId);
    
    UserDto fetch(String name, String sessionId);

    void generateServerSecret(User user);
}