package com.github.maxamel.server.domain;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.repositories.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;
    
    @Value("{test.username}")
    private String username;
    
    @Value("${test.passwordless}")
    private String pass;

    @Test
    public void findOneShouldSuccessTest() {
        User persist = entityManager.persist(User.builder()
                .name(username)
                .passwordless(pass)
                .build());

        Optional<User> user = repository.findOne(persist.getId());
        assertTrue(user.isPresent());
        assertThat(user.get().getName(), is(equalTo(username)));
        assertThat(user.get().getPasswordless(), is(equalTo(pass)));
    }
    
    @Test
    public void findByNameShouldSuccessTest() {
        User persist = entityManager.persist(User.builder()
                .name(username)
                .passwordless(pass)
                .build());

        Optional<User> user = repository.findByName(persist.getName());
        assertTrue(user.isPresent());
        assertThat(user.get().getName(), is(equalTo(username)));
        assertThat(user.get().getPasswordless(), is(equalTo(pass)));
    }
    
    @Test
    public void removeShouldSuccessTest() {
        User persist = entityManager.persist(User.builder()
                .name(username)
                .passwordless(pass)
                .build());

        repository.delete(persist.getId());
        Optional<User> user = repository.findOne(persist.getId());
        assertTrue(!user.isPresent());

    }
    
    @Test
    public void removeByNameShouldSuccessTest() {
        User persist = entityManager.persist(User.builder()
                .name(username)
                .passwordless(pass)
                .build());

        repository.deleteByName(persist.getName());
        Optional<User> user = repository.findOne(persist.getId());
        assertTrue(!user.isPresent());

    }
    
    @Test
    public void findAllShouldSuccessTest() {
        User persist1 = entityManager.persist(User.builder()
                .name(username)
                .passwordless(pass)
                .build());
        
        User persist2 = entityManager.persist(User.builder()
                .name("Mike")
                .passwordless(pass)
                .build());

        List<User> users = repository.findAll();
        
        assertTrue(users.size() == 2);
        assertTrue(users.get(0).equals(persist1));
        assertTrue(users.get(1).equals(persist2));
    }

}
