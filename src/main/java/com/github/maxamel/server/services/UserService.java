package com.github.maxamel.server.services;

import com.github.maxamel.server.web.dtos.UserDto;

/**
 * @author Idan Rozenfeld
 */
public interface UserService {

    UserDto register(UserDto dto);

    void remove(long id);
   
    void removeByName(String name);

    UserDto get(String name);   // requires valid session
    
    UserDto updateToken(UserDto dto);

}