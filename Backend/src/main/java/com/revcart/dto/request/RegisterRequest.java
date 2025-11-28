package com.revcart.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revcart.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6, max = 64)
    private String password;

    private String phone;

    private UserRole role = UserRole.CUSTOMER;

    @JsonProperty("name")
    public void setAliasName(String name) {
        this.fullName = name;
    }
}

