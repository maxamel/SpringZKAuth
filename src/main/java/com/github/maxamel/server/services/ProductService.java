package com.github.maxamel.server.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.maxamel.server.web.dtos.ProductDto;

/**
 * @author Idan Rozenfeld
 */
public interface ProductService {

    ProductDto catalogue(ProductDto empDto);

    Page<ProductDto> fetch(Pageable pageable);

    void remove(long id);

    ProductDto get(long id);

    float getProductPriceAvg();
}