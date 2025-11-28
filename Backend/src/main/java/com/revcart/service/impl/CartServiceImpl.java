package com.revcart.service.impl;

import com.revcart.dto.CartDto;
import com.revcart.dto.request.CartItemRequest;
import com.revcart.entity.Cart;
import com.revcart.entity.CartItem;
import com.revcart.entity.Product;
import com.revcart.entity.User;
import com.revcart.exception.BadRequestException;
import com.revcart.exception.ResourceNotFoundException;
import com.revcart.mapper.CartMapper;
import com.revcart.repository.CartRepository;
import com.revcart.repository.ProductRepository;
import com.revcart.repository.UserRepository;
import com.revcart.service.CartService;
import java.math.BigDecimal;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class CartServiceImpl implements CartService {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 100;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(
            CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CartDto getMyCart() {
        Cart cart = getOrCreateCart(getCurrentUser());
        return CartMapper.toDto(cart);
    }

    @Override
    public CartDto addItem(CartItemRequest request) {
        return executeWithRetry(() -> {
            Cart cart = getOrCreateCartWithLock(getCurrentUser());
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            if (!product.isActive()) {
                throw new BadRequestException("Product inactive");
            }
            CartItem item = cart.getItems().stream()
                    .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        CartItem ci = new CartItem();
                        ci.setProduct(product);
                        ci.setCart(cart);
                        ci.setQuantity(0);
                        ci.setPrice(product.getPrice());
                        cart.getItems().add(ci);
                        return ci;
                    });
            item.setQuantity(item.getQuantity() + request.getQuantity());
            updateCartTotal(cart);
            cartRepository.save(cart);
            return CartMapper.toDto(cart);
        });
    }

    @Override
    public CartDto updateItem(CartItemRequest request) {
        return executeWithRetry(() -> {
            Cart cart = getOrCreateCartWithLock(getCurrentUser());
            CartItem item = cart.getItems().stream()
                    .filter(ci -> ci.getProduct().getId().equals(request.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));
            item.setQuantity(request.getQuantity());
            updateCartTotal(cart);
            cartRepository.save(cart);
            return CartMapper.toDto(cart);
        });
    }

    @Override
    public CartDto removeItem(Long productId) {
        return executeWithRetry(() -> {
            Cart cart = getOrCreateCartWithLock(getCurrentUser());
            cart.getItems().removeIf(ci -> ci.getProduct().getId().equals(productId));
            updateCartTotal(cart);
            cartRepository.save(cart);
            return CartMapper.toDto(cart);
        });
    }

    @Override
    public void clearCart() {
        executeWithRetryVoid(() -> {
            Cart cart = getOrCreateCartWithLock(getCurrentUser());
            cart.getItems().clear();
            cart.setTotalAmount(BigDecimal.ZERO);
            cartRepository.save(cart);
        });
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private Cart getOrCreateCartWithLock(User user) {
        // Try to get existing cart with lock first
        return cartRepository.findByUserWithLock(user).orElseGet(() -> {
            // If cart doesn't exist, create a new one
            // Use a separate transaction to avoid deadlock on creation
            try {
                Cart cart = new Cart();
                cart.setUser(user);
                return cartRepository.save(cart);
            } catch (Exception e) {
                // If creation fails (e.g., cart was created by another thread), try to fetch it again
                return cartRepository.findByUserWithLock(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Failed to create or retrieve cart"));
            }
        });
    }

    private <T> T executeWithRetry(java.util.function.Supplier<T> operation) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                return operation.get();
            } catch (CannotAcquireLockException | LockAcquisitionException e) {
                lastException = e;
                attempts++;
                if (attempts < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempts); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            } catch (JpaSystemException e) {
                // Check if it's a deadlock exception
                Throwable cause = e.getCause();
                if (cause instanceof LockAcquisitionException ||
                    cause instanceof CannotAcquireLockException ||
                    (cause != null && cause.getMessage() != null &&
                     cause.getMessage().contains("Deadlock"))) {
                    lastException = e;
                    attempts++;
                    if (attempts < MAX_RETRY_ATTEMPTS) {
                        try {
                            Thread.sleep(RETRY_DELAY_MS * attempts);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Retry interrupted", ie);
                        }
                    }
                } else {
                    throw e;
                }
            }
        }

        throw new RuntimeException("Failed to acquire lock after " + MAX_RETRY_ATTEMPTS + " attempts", lastException);
    }

    private void executeWithRetryVoid(java.lang.Runnable operation) {
        executeWithRetry(() -> {
            operation.run();
            return null;
        });
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}

