package com.revcart.dto;

import com.revcart.enums.UserRole;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private boolean emailVerified;
    private List<AddressDto> addresses;
}

