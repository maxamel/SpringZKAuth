package com.github.maxamel.server.services.impl;

import com.github.maxamel.server.web.dtos.UserDto;
import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Max Amelchenko
 */
@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper mapper;

    private final UserRepository repository;
   
    @Autowired
    public UserServiceImpl(ModelMapper mapper, UserRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDto register(UserDto dto) {
        User user = mapper.map(dto, User.class);
        User newuser = repository.save(user);
        return mapper.map(newuser, UserDto.class);
    }

    @Override
    @Transactional
    public void remove(long id) {
        repository.findOne(id).orElseThrow(() -> new EmptyResultDataAccessException("No user found with id: " + id, 1));
        repository.delete(id);
    }
    
    @Override
    @Transactional
    public void removeByName(String name) {
        repository.findByName(name).orElseThrow(() -> new EmptyResultDataAccessException("No user found with name: " + name, 1));
        repository.deleteByName(name);
    }

}