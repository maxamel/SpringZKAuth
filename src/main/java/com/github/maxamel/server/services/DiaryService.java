package com.github.maxamel.server.services;

import java.util.List;

import com.github.maxamel.server.web.dtos.DiaryDto;

/**
 * @author Max Amelchenko
 */
public interface DiaryService {

	DiaryDto add(DiaryDto dto, String sessionId);

    void removeByUsernameAndEntryname(String username, String entryname, String sessionId);
    
    DiaryDto fetch(String username, String entryname, String sessionId);

    List<DiaryDto> fetchByUsername(String username, String sessionId);
}