package com.revcart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @NotBlank
    private String fullName;
    private String phone;
    private String avatarUrl;
}

