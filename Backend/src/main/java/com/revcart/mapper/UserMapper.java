package com.revcart.mapper;

import com.revcart.dto.AddressDto;
import com.revcart.dto.UserDto;
import com.revcart.entity.User;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .addresses(user.getAddresses().stream().map(AddressMapper::toDto).collect(Collectors.toList()))
                .build();
    }

    public static AddressDto toDto(com.revcart.entity.Address address) {
        return AddressMapper.toDto(address);
    }
}

