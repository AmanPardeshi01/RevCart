package com.revcart.dto.request;

import com.revcart.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentCaptureRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod method;

    private Long amount;

    @NotBlank
    private String providerPaymentId;

    private String signature;
}

