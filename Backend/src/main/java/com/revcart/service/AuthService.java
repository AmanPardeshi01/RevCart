package com.revcart.service;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.AuthResponse;
import com.revcart.dto.UserDto;
import com.revcart.dto.request.AuthRequest;
import com.revcart.dto.request.OtpVerificationRequest;
import com.revcart.dto.request.PasswordResetRequest;
import com.revcart.dto.request.RegisterRequest;

public interface AuthService {
    ApiResponse<String> register(RegisterRequest request);
    ApiResponse<AuthResponse> login(AuthRequest request);
    ApiResponse<String> verifyOtp(OtpVerificationRequest request);
    ApiResponse<String> resendOtp(String email);
    ApiResponse<String> forgotPassword(String email);
    ApiResponse<UserDto> resetPassword(PasswordResetRequest request);
}

