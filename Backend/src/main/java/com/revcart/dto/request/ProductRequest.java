package com.revcart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(0)
    private BigDecimal price;

    private BigDecimal discount;

    private String imageUrl;

    private boolean active = true;

    @NotNull
    private Long categoryId;

    private Integer quantity = 0;

    private Set<String> highlights;
}

