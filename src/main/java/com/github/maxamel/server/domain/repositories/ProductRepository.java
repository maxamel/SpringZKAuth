package com.github.maxamel.server.domain.repositories;

import com.github.maxamel.server.domain.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * @author Idan Rozenfeld
 */
@RepositoryDefinition(domainClass = Product.class, idClass = Long.class)
public interface ProductRepository {
    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findOne(long id);

    Product save(Product product);

    void delete(long id);

}