package com.revcart.controller;

import com.revcart.dto.AddressDto;
import com.revcart.dto.ApiResponse;
import com.revcart.dto.UserDto;
import com.revcart.dto.request.ChangePasswordRequest;
import com.revcart.dto.request.ProfileUpdateRequest;
import com.revcart.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDto me() {
        return userService.getCurrentUser();
    }

    @PutMapping
    public ApiResponse<UserDto> update(@Valid @RequestBody ProfileUpdateRequest request) {
        UserDto updated = userService.updateProfile(request);
        return ApiResponse.<UserDto>builder()
                .success(true)
                .data(updated)
                .message("Profile updated successfully")
                .build();
    }

    @GetMapping("/addresses")
    public ApiResponse<List<AddressDto>> getAddresses() {
        List<AddressDto> addresses = userService.getAddresses();
        return ApiResponse.<List<AddressDto>>builder()
                .success(true)
                .data(addresses)
                .message("Addresses retrieved successfully")
                .build();
    }

    @PostMapping("/address")
    public ApiResponse<AddressDto> addAddress(@Valid @RequestBody AddressDto request) {
        AddressDto dto = userService.addAddress(request);
        return ApiResponse.<AddressDto>builder()
                .success(true)
                .data(dto)
                .message("Address saved successfully")
                .build();
    }

    @PutMapping("/address/{id}")
    public ApiResponse<AddressDto> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDto request) {
        AddressDto dto = userService.updateAddress(id, request);
        return ApiResponse.<AddressDto>builder()
                .success(true)
                .data(dto)
                .message("Address updated successfully")
                .build();
    }

    @DeleteMapping("/address/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Address deleted successfully")
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Password changed successfully")
                .build();
    }
}

