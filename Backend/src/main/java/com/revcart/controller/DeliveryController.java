package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.OrderDto;
import com.revcart.dto.PagedResponse;
import com.revcart.dto.request.OrderStatusUpdateRequest;
import com.revcart.enums.OrderStatus;
import com.revcart.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final OrderService orderService;

    public DeliveryController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = orderService.getDeliveryStatistics();
        return ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .data(stats)
                .message("Delivery statistics retrieved")
                .build();
    }

    @GetMapping("/orders/assigned")
    public ApiResponse<List<OrderDto>> getAssignedOrders() {
        List<OrderDto> orders = orderService.getAssignedOrders();
        return ApiResponse.<List<OrderDto>>builder()
                .success(true)
                .data(orders)
                .message("Assigned orders retrieved")
                .build();
    }

    @GetMapping("/orders/in-transit")
    public ApiResponse<List<OrderDto>> getInTransitOrders() {
        List<OrderDto> orders = orderService.getInTransitOrders();
        return ApiResponse.<List<OrderDto>>builder()
                .success(true)
                .data(orders)
                .message("In transit orders retrieved")
                .build();
    }

    @GetMapping("/orders/pending")
    public ApiResponse<List<OrderDto>> getPendingOrders() {
        List<OrderDto> orders = orderService.getPendingOrders();
        return ApiResponse.<List<OrderDto>>builder()
                .success(true)
                .data(orders)
                .message("Pending orders retrieved")
                .build();
    }

    @GetMapping("/orders")
    public PagedResponse<OrderDto> myDeliveries(
            @RequestParam OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.deliveryOrders(status, PageRequest.of(page, size));
    }

    @GetMapping("/orders/{id}")
    public OrderDto getAssignedOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping("/orders/{id}/status")
    public ApiResponse<OrderDto> updateStatus(
            @PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderDto dto = orderService.updateStatus(id, request);
        return ApiResponse.<OrderDto>builder().success(true).data(dto).message("Status updated").build();
    }
}

