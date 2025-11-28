package com.revcart.mapper;

import com.revcart.dto.ProductDto;
import com.revcart.entity.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .sku(product.getSku())
                .brand(product.getBrand())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .availableQuantity(product.getInventory() != null ? product.getInventory().getAvailableQuantity() : 0)
                .build();
    }
}

