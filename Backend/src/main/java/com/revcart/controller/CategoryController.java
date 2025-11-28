package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.CategoryDto;
import com.revcart.dto.request.CategoryRequest;
import com.revcart.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> list() {
        return categoryService.list();
    }

    // Note: POST, PUT, DELETE are now handled by AdminCategoryController
    // This controller only handles public GET requests
}

