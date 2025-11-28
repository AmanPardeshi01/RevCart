package com.revcart.dto;

import com.revcart.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String name;
    private UserRole role;
}

