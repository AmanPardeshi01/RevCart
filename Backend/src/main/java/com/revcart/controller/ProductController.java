package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.PagedResponse;
import com.revcart.dto.ProductDto;
import com.revcart.dto.request.ProductRequest;
import com.revcart.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public PagedResponse<ProductDto> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.list(keyword, pageable);
    }

    @GetMapping("/products/{id}")
    public ProductDto get(@PathVariable Long id) {
        return productService.get(id);
    }

    @PostMapping("/admin/products")
    public ProductDto create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/admin/products/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/admin/products/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.<String>builder().success(true).message("Product archived").build();
    }
}

