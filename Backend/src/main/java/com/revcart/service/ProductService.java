package com.revcart.service;

import com.revcart.dto.PagedResponse;
import com.revcart.dto.ProductDto;
import com.revcart.dto.request.ProductRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDto create(ProductRequest request);
    ProductDto update(Long id, ProductRequest request);
    void delete(Long id);
    ProductDto get(Long id);
    PagedResponse<ProductDto> list(String keyword, Pageable pageable);
    List<ProductDto> getFeatured();
}

