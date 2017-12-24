package com.github.maxamel.server.web.controllers;

import com.github.maxamel.server.web.dtos.ProductDto;
import com.github.maxamel.server.web.dtos.errors.ErrorDto;
import com.github.maxamel.server.services.ProductService;
import com.github.rozidan.springboot.logger.Loggable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Idan Rozenfeld
 */
@Loggable(ignore = Exception.class)
@Api(tags = "Products")
@RestController
@RequestMapping(path = "/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "Catalogue new product or update existing one")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully catalogue product"),
            @ApiResponse(code = 428, message = "Invalid product info", response = ErrorDto.class)})
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public ProductDto catalogue(@ApiParam(value = "Product data", name = "Product",
            required = true) @Validated @RequestBody ProductDto productDto) {
        return productService.catalogue(productDto);
    }

    @ApiOperation(value = "Fetch all products with paging")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Number of records per page")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully lists Products")})
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping
    public Page<ProductDto> fetch(@ApiIgnore @PageableDefault final Pageable pageable) {
        return productService.fetch(pageable);
    }

    @ApiOperation("Remove product from catalog")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Product has been removed from catalog"),
            @ApiResponse(code = 404, message = "Product not found")})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        productService.remove(id);
    }

    @ApiOperation("Retrieving existing product")
    @ApiResponse(code = 200, message = "Product has been fetched")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ProductDto retrieve(@PathVariable long id) {
        return productService.get(id);
    }

    @ApiOperation("Gets the products average")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Product has been fetched"),
            @ApiResponse(code = 404, message = "Product not found")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/avg")
    public float productPriceAvg() {
        return productService.getProductPriceAvg();
    }

    @ApiOperation("Query product from catalog")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Product has been query from catalog"),
            @ApiResponse(code = 404, message = "Product not found")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/query")
    public ProductDto query(@RequestParam long id) {
        return productService.get(id);
    }
}