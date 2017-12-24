package com.github.maxamel.server.domain;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.maxamel.server.domain.model.Product;
import com.github.maxamel.server.domain.model.types.ProductCategory;
import com.github.maxamel.server.domain.repositories.ProductRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository repository;

    @Test
    public void findOneShouldSuccessTest() {
        Product persist = entityManager.persist(Product.builder()
                .name("John")
                .category(ProductCategory.GAME)
                .unitPrice(100F)
                .desc("desc")
                .build());

        Optional<Product> product = repository.findOne(persist.getId());
        assertTrue(product.isPresent());
        assertThat(product.get().getName(), is(equalTo("John")));
        assertThat(product.get().getCategory(), is(equalTo(ProductCategory.GAME)));
        assertThat(product.get().getUnitPrice(), is(equalTo(100F)));
        assertThat(product.get().getDesc(), is(equalTo("desc")));
    }

}
