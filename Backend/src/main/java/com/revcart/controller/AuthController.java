package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.AuthResponse;
import com.revcart.dto.AuthResponse;
import com.revcart.dto.UserDto;
import com.revcart.dto.request.AuthRequest;
import com.revcart.dto.request.OtpVerificationRequest;
import com.revcart.dto.request.PasswordResetRequest;
import com.revcart.dto.request.RegisterRequest;
import com.revcart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        return authService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    public ApiResponse<String> resendOtp(@RequestParam String email) {
        return authService.resendOtp(email);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ApiResponse<UserDto> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        return authService.resetPassword(request);
    }
}

