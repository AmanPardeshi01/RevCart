package com.revcart.mapper;

import com.revcart.dto.OrderDto;
import com.revcart.dto.OrderItemDto;
import com.revcart.dto.PaymentDto;
import com.revcart.entity.Order;
import com.revcart.entity.OrderItem;
import com.revcart.entity.Payment;
import java.util.stream.Collectors;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber("ORD-" + String.format("%06d", order.getId()))
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .shippingAddress(AddressMapper.toDto(order.getShippingAddress()))
                .items(order.getItems().stream().map(OrderMapper::mapItem).collect(Collectors.toList()))
                .payment(mapPayment(order.getPayment()))
                .deliveryAgentName(order.getDeliveryAgent() != null ? order.getDeliveryAgent().getFullName() : null)
                .user(order.getUser() != null ? UserMapper.toDto(order.getUser()) : null)
                .build();
    }

    private static OrderItemDto mapItem(OrderItem item) {
        return OrderItemDto.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(item.getProduct().getImageUrl())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    private static PaymentDto mapPayment(Payment payment) {
        if (payment == null) {
            return null;
        }
        return PaymentDto.builder()
                .id(payment.getId())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .providerPaymentId(payment.getProviderPaymentId())
                .paidAt(payment.getPaidAt())
                .build();
    }
}

