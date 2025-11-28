package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.PaymentDto;
import com.revcart.dto.request.PaymentCaptureRequest;
import com.revcart.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{orderId}/initiate")
    public PaymentDto initiate(@PathVariable Long orderId) {
        return paymentService.initiatePayment(orderId);
    }

    @PostMapping("/capture")
    public ApiResponse<PaymentDto> capture(@Valid @RequestBody PaymentCaptureRequest request) {
        PaymentDto dto = paymentService.capturePayment(request);
        return ApiResponse.<PaymentDto>builder().success(true).data(dto).message("Payment captured").build();
    }
}

