package com.github.maxamel.server.services;

import com.github.maxamel.server.web.dtos.UserDto;

/**
 * @author Max Amelchenko
 */
public interface UserService {

    UserDto register(UserDto dto);

    void remove(long id, String sessionId);
    
    void removeByName(String name, String sessionId);
    
    UserDto example(String name, String sessionId);

}