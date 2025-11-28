package com.revcart.service;

import com.revcart.dto.CategoryDto;
import com.revcart.dto.request.CategoryRequest;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> list();
    CategoryDto create(CategoryRequest request);
    CategoryDto update(Long id, CategoryRequest request);
    void delete(Long id);
    CategoryDto getById(Long id);
}

