package com.github.maxamel.server.domain.repositories;

import com.github.maxamel.server.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * @author Max Amelchenko
 */
@RepositoryDefinition(domainClass = User.class, idClass = Long.class)
public interface UserRepository {
    
    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findOne(long id);
    
    Optional<User> findByName(String name);

    User save(User user);

    void delete(long id);
    
    void deleteByName(String name);
}