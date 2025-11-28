package com.revcart.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private String imageUrl;
    private boolean active;
    private String sku;
    private String brand;
    private String categoryName;
    private Long categoryId;
    private Integer availableQuantity;
}

