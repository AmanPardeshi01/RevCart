package com.revcart.mapper;

import com.revcart.dto.CartDto;
import com.revcart.dto.CartItemDto;
import com.revcart.entity.Cart;
import com.revcart.entity.CartItem;
import java.math.BigDecimal;
import java.util.stream.Collectors;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }
        return CartDto.builder()
                .id(cart.getId())
                .items(cart.getItems().stream().map(CartMapper::mapItem).collect(Collectors.toList()))
                .totalAmount(cart.getItems().stream()
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

    private static CartItemDto mapItem(CartItem item) {
        return CartItemDto.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .imageUrl(item.getProduct().getImageUrl())
                .build();
    }
}

