package com.github.maxamel.server.web.controllers;

import com.github.maxamel.server.web.dtos.DiaryDto;
import com.github.maxamel.server.web.dtos.errors.ErrorDto;
import com.github.maxamel.server.services.DiaryService;
import com.github.rozidan.springboot.logger.Loggable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

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
@Api(tags = "Diaries")
@RestController
@RequestMapping(path = "/diary")
public class DiaryController {

    private final DiaryService diaryService;
    
    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @ApiOperation(value = "Add new entry")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created entry"),
            @ApiResponse(code = 409, message = "Entry already exists"),
            @ApiResponse(code = 428, message = "Invalid user info", response = ErrorDto.class)})
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public DiaryDto add(@Validated @RequestBody DiaryDto dto, @RequestHeader(value="ZKAuth-Token", required=false) String token) {
    	return diaryService.add(dto, token);
    }

    @ApiOperation("Delete entry")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Entrys has been removed"),
            @ApiResponse(code = 401, message = "Unauthorized Access"),
            @ApiResponse(code = 404, message = "Entry not found")})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{username}/{entryname}")
    public void remove(@PathVariable String username, @PathVariable String entryname, @RequestHeader(value="ZKAuth-Token", required=false) String token) {
    	diaryService.removeByUsernameAndEntryname(username, entryname, token);
    }

    @ApiOperation("Retrieving existing user")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Successfully fetched entry list"),
        @ApiResponse(code = 401, message = "Unauthorized Access"),
        @ApiResponse(code = 404, message = "No entries Found")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    public List<DiaryDto> fetch(@PathVariable String username, @RequestHeader(value="ZKAuth-Token", required=false) String token) {
        return diaryService.fetchByUsername(username, token);
    }
}