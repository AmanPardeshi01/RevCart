package com.revcart.dto;

import com.revcart.enums.OrderStatus;
import com.revcart.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private Instant updatedAt;
    private AddressDto shippingAddress;
    private List<OrderItemDto> items;
    private PaymentDto payment;
    private String deliveryAgentName;
    private UserDto user;
}

