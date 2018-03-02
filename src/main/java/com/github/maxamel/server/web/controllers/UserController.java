package com.github.maxamel.server.web.controllers;

import com.github.maxamel.server.web.dtos.UserDto;
import com.github.maxamel.server.web.dtos.errors.ErrorDto;
import com.github.maxamel.server.services.UserService;
import com.github.rozidan.springboot.logger.Loggable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Max Amelchenko
 */
@Loggable(ignore = Exception.class)
@Api(tags = "Users")
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Register new user")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully registered user"),
            @ApiResponse(code = 409, message = "User already exists"),
            @ApiResponse(code = 428, message = "Invalid user info", response = ErrorDto.class)})
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public UserDto register(@Validated @RequestBody UserDto dto) {
    	return userService.register(dto);
    }

    @ApiOperation("Delete user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User has been removed"),
            @ApiResponse(code = 401, message = "Unauthorized Access"),
            @ApiResponse(code = 404, message = "User not found")})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{name}")
    public void remove(@PathVariable String name, @RequestHeader(value="ZKAuth-Token", required=false) String token) {
        userService.removeByName(name, token);
    }

    @ApiOperation("Retrieving existing user")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Successfully fetched user"),
        @ApiResponse(code = 401, message = "Unauthorized Access"),
        @ApiResponse(code = 404, message = "User not Found")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{name}")
    public UserDto fetch(@PathVariable String name, @RequestHeader(value="ZKAuth-Token", required=false) String token) {
        return userService.fetch(name, token);
    }
}