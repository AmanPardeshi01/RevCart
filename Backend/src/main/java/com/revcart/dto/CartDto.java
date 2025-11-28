package com.revcart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartDto {
    private Long id;
    private List<CartItemDto> items;
    private BigDecimal totalAmount;
}

