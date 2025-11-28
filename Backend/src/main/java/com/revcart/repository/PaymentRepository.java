package com.revcart.repository;

import com.revcart.entity.Order;
import com.revcart.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
    Optional<Payment> findByProviderPaymentId(String providerPaymentId);
}

