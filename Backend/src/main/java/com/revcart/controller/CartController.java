package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.CartDto;
import com.revcart.dto.request.CartItemRequest;
import com.revcart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartDto getCart() {
        return cartService.getMyCart();
    }

    @PostMapping
    public CartDto addItem(@Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(request);
    }

    @PatchMapping
    public CartDto updateItem(@Valid @RequestBody CartItemRequest request) {
        return cartService.updateItem(request);
    }

    @DeleteMapping("/{productId}")
    public CartDto removeItem(@PathVariable Long productId) {
        return cartService.removeItem(productId);
    }

    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart() {
        cartService.clearCart();
        return ApiResponse.<String>builder().success(true).message("Cart cleared").build();
    }
}

