package com.revcart.service.impl;

import com.revcart.dto.PagedResponse;
import com.revcart.dto.ProductDto;
import com.revcart.dto.request.ProductRequest;
import com.revcart.entity.Category;
import com.revcart.entity.Inventory;
import com.revcart.entity.Product;
import com.revcart.exception.ResourceNotFoundException;
import com.revcart.mapper.ProductMapper;
import com.revcart.repository.CategoryRepository;
import com.revcart.repository.InventoryRepository;
import com.revcart.repository.ProductRepository;
import com.revcart.service.ProductService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Product product = new Product();
        mapProduct(product, request, category);
        Product saved = productRepository.save(product);
        createOrUpdateInventory(saved, request.getQuantity());
        return ProductMapper.toDto(saved);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        mapProduct(product, request, category);
        createOrUpdateInventory(product, request.getQuantity());
        return ProductMapper.toDto(productRepository.save(product));
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public ProductDto get(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    @Cacheable(value = "products", key = "#keyword + '-' + #pageable.pageNumber")
    public PagedResponse<ProductDto> list(String keyword, Pageable pageable) {
        Page<Product> page = productRepository.searchActiveProducts(keyword, pageable);
        return PagedResponse.<ProductDto>builder()
                .content(page.getContent().stream().map(ProductMapper::toDto).collect(Collectors.toList()))
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .page(page.getNumber())
                .size(page.getSize())
                .build();
    }

    @Override
    @Cacheable("featuredProducts")
    public List<ProductDto> getFeatured() {
        return productRepository.findTop12ByOrderByCreatedAtDesc().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    private void mapProduct(Product product, ProductRequest request, Category category) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.isActive());
        product.setCategory(category);
    }

    private void createOrUpdateInventory(Product product, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProduct(product).orElseGet(() -> {
            Inventory inv = new Inventory();
            inv.setProduct(product);
            return inv;
        });
        inventory.setAvailableQuantity(quantity != null ? quantity : 0);
        inventoryRepository.save(inventory);
    }
}

