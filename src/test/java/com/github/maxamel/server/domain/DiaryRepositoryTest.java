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

import com.github.maxamel.server.domain.model.Diary;
import com.github.maxamel.server.domain.repositories.DiaryRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DiaryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DiaryRepository repository;
    
    @Value("{test.username}")
    private String username;
    
    @Value("${test.entryname}")
    private String entryname;
    
    @Value("${test.pass}")
    private String content;

    @Test
    public void findOneShouldSuccessTest() {
        Diary persist = entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname)
                .content(content)
                .build());

        Optional<Diary> diary = repository.findOne(persist.getId());
        assertTrue(diary.isPresent());
        assertThat(diary.get().getUsername(), is(equalTo(username)));
        assertThat(diary.get().getEntryname(), is(equalTo(entryname)));
        assertThat(diary.get().getContent(), is(equalTo(content)));
    }
    
    @Test
    public void findByUsernameShouldSuccessTest() {
        Diary persist1 = entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname)
                .content(content)
                .build());
        
        Diary persist2 = entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname+"1")
                .content(content)
                .build());

        List<Diary> list = repository.findByUsername(username);
        assertTrue(list.size() == 2);
        assertTrue(list.get(0).getId().equals(persist1.getId()));
        assertTrue(list.get(1).getId().equals(persist2.getId()));
    }
    
    @Test
    public void removeShouldSuccessTest() {
        Diary persist = entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname)
                .content(content)
                .build());

        repository.delete(persist.getId());
        Optional<Diary> diary = repository.findOne(persist.getId());
        assertTrue(!diary.isPresent());

    }
    
    @Test
    public void removeByUsernameAndEntrynameShouldSuccessTest() {
        Diary persist = entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname)
                .content(content)
                .build());

        repository.deleteByUsernameAndEntryname(persist.getUsername(), persist.getEntryname());
        Optional<Diary> diary = repository.findOne(persist.getId());
        assertTrue(!diary.isPresent());
    }
    
    @Test
    public void findAllShouldSuccessTest() {
        Diary persist1 = entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname)
                .content(content)
                .build());
        
        Diary persist2 = entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname+"1")
                .content(content)
                .build());
        
        Diary persist3 = entityManager.persist(Diary.builder()
                .username(username+"1")
                .entryname(entryname)
                .content(content)
                .build());

        List<Diary> list = repository.findAll();
        assertTrue(list.size() == 3);
        assertTrue(list.get(0).getId().equals(persist1.getId()));
        assertTrue(list.get(1).getId().equals(persist2.getId()));
        assertTrue(list.get(2).getId().equals(persist3.getId()));
    }
    
    @Test
    public void findByUsernameAndEntrynameShouldSuccessTest() {
        Diary persist1 = entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname)
                .content(content)
                .build());
        
        entityManager.persist(Diary.builder()
                .username(username)
                .entryname(entryname+"1")
                .content(content)
                .build());
        
        entityManager.persist(Diary.builder()
                .username(username+"1")
                .entryname(entryname)
                .content(content)
                .build());

        Optional<Diary> diary = repository.findByUsernameAndEntryname(persist1.getUsername(), persist1.getEntryname());
        assertTrue(diary.isPresent());
        assertThat(diary.get().getUsername(), is(equalTo(username)));
        assertThat(diary.get().getEntryname(), is(equalTo(entryname)));
        assertThat(diary.get().getContent(), is(equalTo(content)));
    }

}
