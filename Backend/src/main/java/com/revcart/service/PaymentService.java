package com.revcart.service;

import com.revcart.dto.PaymentDto;
import com.revcart.dto.request.PaymentCaptureRequest;

public interface PaymentService {
    PaymentDto initiatePayment(Long orderId);
    PaymentDto capturePayment(PaymentCaptureRequest request);
    void handleRefund(Long orderId);
}

