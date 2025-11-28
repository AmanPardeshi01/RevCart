package com.revcart.service;

import com.revcart.dto.OrderDto;
import com.revcart.dto.PagedResponse;
import com.revcart.dto.request.CheckoutRequest;
import com.revcart.dto.request.OrderStatusUpdateRequest;
import com.revcart.enums.OrderStatus;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto checkout(CheckoutRequest request);
    PagedResponse<OrderDto> myOrders(Pageable pageable);
    PagedResponse<OrderDto> allOrders(Pageable pageable);
    OrderDto getOrder(Long orderId);
    OrderDto updateStatus(Long orderId, OrderStatusUpdateRequest request);
    OrderDto assignDeliveryAgent(Long orderId, Long agentId);
    OrderDto cancelOrder(Long orderId, String reason);
    PagedResponse<OrderDto> deliveryOrders(OrderStatus status, Pageable pageable);
    Map<String, Object> getDeliveryStatistics();
    List<OrderDto> getAssignedOrders();
    List<OrderDto> getInTransitOrders();
    List<OrderDto> getPendingOrders();
}

