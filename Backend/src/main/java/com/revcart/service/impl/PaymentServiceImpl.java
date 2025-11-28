package com.revcart.service.impl;

import com.revcart.dto.PaymentDto;
import com.revcart.dto.request.PaymentCaptureRequest;
import com.revcart.entity.Order;
import com.revcart.entity.Payment;
import com.revcart.enums.PaymentMethod;
import com.revcart.enums.PaymentStatus;
import com.revcart.exception.BadRequestException;
import com.revcart.exception.ResourceNotFoundException;
import com.revcart.mapper.OrderMapper;
import com.revcart.repository.OrderRepository;
import com.revcart.repository.PaymentRepository;
import com.revcart.service.PaymentService;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public PaymentDto initiatePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Payment payment = paymentRepository.findByOrder(order).orElseGet(() -> {
            Payment p = new Payment();
            p.setOrder(order);
            p.setAmount(order.getTotalAmount());
            p.setCurrency("INR");
            return p;
        });
        payment.setStatus(PaymentStatus.PENDING);
        return map(paymentRepository.save(payment));
    }

    @Override
    public PaymentDto capturePayment(PaymentCaptureRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new BadRequestException("Payment not initiated"));
        payment.setMethod(request.getMethod());
        payment.setProviderPaymentId(request.getProviderPaymentId());
        payment.setPaymentSignature(request.getSignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(Instant.now());
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        orderRepository.save(order);
        return map(paymentRepository.save(payment));
    }

    @Override
    public void handleRefund(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Payment payment = paymentRepository.findByOrder(order).orElse(null);
        if (payment == null) {
            return;
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundReferenceId("REF-" + payment.getId());
        paymentRepository.save(payment);
    }

    private PaymentDto map(Payment payment) {
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

