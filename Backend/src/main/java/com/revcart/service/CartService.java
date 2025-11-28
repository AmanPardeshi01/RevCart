package com.revcart.service;

import com.revcart.dto.CartDto;
import com.revcart.dto.request.CartItemRequest;

public interface CartService {
    CartDto getMyCart();
    CartDto addItem(CartItemRequest request);
    CartDto updateItem(CartItemRequest request);
    CartDto removeItem(Long productId);
    void clearCart();
}

