package com.revcart.dto;

import com.revcart.enums.PaymentMethod;
import com.revcart.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDto {
    private Long id;
    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;
    private String providerPaymentId;
    private Instant paidAt;
}

