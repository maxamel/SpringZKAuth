package com.github.maxamel.server.domain;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Test
    public void findOneShouldSuccessTest() {
        User persist = entityManager.persist(User.builder()
                .name("John")
                .token(new BigInteger("7896324669876116"))
                .build());

        Optional<User> user = repository.findOne(persist.getId());
        assertTrue(user.isPresent());
        assertThat(user.get().getName(), is(equalTo("John")));
        assertThat(user.get().getToken(), is(equalTo(new BigInteger("7896324669876116"))));
    }

}
