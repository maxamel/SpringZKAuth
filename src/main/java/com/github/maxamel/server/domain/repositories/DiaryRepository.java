package com.github.maxamel.server.domain.repositories;

import com.github.maxamel.server.domain.model.Diary;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * @author Max Amelchenko
 */
@RepositoryDefinition(domainClass = Diary.class, idClass = Long.class)
public interface DiaryRepository {
    
    List<Diary> findAll();

    Page<Diary> findAll(Pageable pageable);

    Optional<Diary> findOne(long id);
    
    Optional<Diary> findByUsernameAndEntryname(String username, String entryname);
    
    List<Diary> findByUsername(String username);
  
    Diary save(Diary diary);

    void delete(long id);
    
    void deleteByUsernameAndEntryname(String username, String entryname);
}