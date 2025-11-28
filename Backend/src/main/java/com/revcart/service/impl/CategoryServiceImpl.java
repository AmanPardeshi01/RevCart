package com.revcart.service.impl;

import com.revcart.dto.CategoryDto;
import com.revcart.dto.request.CategoryRequest;
import com.revcart.entity.Category;
import com.revcart.exception.BadRequestException;
import com.revcart.exception.ResourceNotFoundException;
import com.revcart.repository.CategoryRepository;
import com.revcart.repository.ProductRepository;
import com.revcart.service.CategoryService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<CategoryDto> list() {
        return categoryRepository.findAll().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto create(CategoryRequest request) {
        Category category = new Category();
        map(category, request);
        return map(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        map(category, request);
        return map(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Check if category is used by any products
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new BadRequestException(
                String.format("Cannot delete category '%s'. It is currently used by %d product(s). " +
                    "Please reassign or remove the products first.", category.getName(), productCount));
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return map(category);
    }

    private CategoryDto map(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .build();
    }

    private void map(Category category, CategoryRequest request) {
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
    }
}

