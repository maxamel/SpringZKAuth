package com.github.maxamel.server.services.impl;

import com.github.maxamel.server.web.dtos.ProductDto;
import com.github.maxamel.server.domain.model.Product;
import com.github.maxamel.server.domain.repositories.ProductRepository;
import com.github.maxamel.server.services.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Idan Rozenfeld
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper mapper;

    private final ProductRepository repository;

    @Autowired
    public ProductServiceImpl(ModelMapper mapper, ProductRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    @Transactional
    public ProductDto catalogue(ProductDto productDto) {
        Product product = mapper.map(productDto, Product.class);
        Product newProd = repository.save(product);
        return mapper.map(newProd, ProductDto.class);
    }

    @Override
    public Page<ProductDto> fetch(Pageable pageable) {
        Page<Product> products = repository.findAll(pageable);
        return products.map(product -> mapper.map(product, ProductDto.class));
    }

    @Override
    @Transactional
    public void remove(long id) {
        repository.delete(id);
    }

    @Override
    public ProductDto get(long id) {
        log.info("get service");
        Product product = repository.findOne(id)
                .orElseThrow(() -> new EmptyResultDataAccessException("No product found with id: " + id, 1));
        return mapper.map(product, ProductDto.class);
    }

    @Override
    public float getProductPriceAvg() {
        List<Product> products = repository.findAll();

        return products.stream()
                .map(Product::getUnitPrice)
                .collect(Collectors.averagingDouble(avg -> avg)).floatValue();
    }

}