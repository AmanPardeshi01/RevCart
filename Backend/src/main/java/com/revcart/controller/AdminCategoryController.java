package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.CategoryDto;
import com.revcart.dto.request.CategoryRequest;
import com.revcart.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryDto>> list() {
        return ApiResponse.<List<CategoryDto>>builder()
                .success(true)
                .data(categoryService.list())
                .message("Categories retrieved successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDto> getById(@PathVariable Long id) {
        return ApiResponse.<CategoryDto>builder()
                .success(true)
                .data(categoryService.getById(id))
                .message("Category retrieved successfully")
                .build();
    }

    @PostMapping
    public ApiResponse<CategoryDto> create(@Valid @RequestBody CategoryRequest request) {
        CategoryDto created = categoryService.create(request);
        return ApiResponse.<CategoryDto>builder()
                .success(true)
                .data(created)
                .message("Category created successfully")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryDto> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        CategoryDto updated = categoryService.update(id, request);
        return ApiResponse.<CategoryDto>builder()
                .success(true)
                .data(updated)
                .message("Category updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Category deleted successfully")
                .build();
    }
}

