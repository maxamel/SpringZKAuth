package com.github.maxamel.server.services;

import java.util.List;

import com.github.maxamel.server.web.dtos.DiaryDto;

/**
 * @author Max Amelchenko
 */
public interface DiaryService {

	public DiaryDto add(DiaryDto dto, String sessionId);
	
	public DiaryDto edit(String username, String entryname, DiaryDto dto, String sessionId);

    public void removeByUsernameAndEntryname(String username, String entryname, String sessionId);
    
    public void removeAll(String username, String sessionId);
    
    public DiaryDto fetch(String username, String entryname, String sessionId);

    public List<DiaryDto> fetchByUsername(String username, String sessionId);
}