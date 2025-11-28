package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.repository.OrderRepository;
import com.revcart.repository.ProductRepository;
import com.revcart.repository.UserRepository;
import java.math.BigDecimal;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AdminDashboardController(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public ApiResponse<DashboardStats> getStats() {
        long totalOrders = orderRepository.count();

        // Calculate total revenue more efficiently
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getTotalAmount() != null)
                .map(order -> order.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalProducts = productRepository.count();

        // Count active users more efficiently
        long activeUsers = userRepository.findAll().stream()
                .filter(user -> user.isActive())
                .count();

        DashboardStats stats = new DashboardStats();
        stats.setTotalOrders(totalOrders);
        stats.setTotalRevenue(totalRevenue);
        stats.setTotalProducts(totalProducts);
        stats.setActiveUsers(activeUsers);

        return ApiResponse.<DashboardStats>builder()
                .success(true)
                .data(stats)
                .message("Dashboard statistics retrieved successfully")
                .build();
    }

    @Data
    public static class DashboardStats {
        private long totalOrders;
        private BigDecimal totalRevenue;
        private long totalProducts;
        private long activeUsers;
    }
}

