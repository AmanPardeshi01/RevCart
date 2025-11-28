package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.PagedResponse;
import com.revcart.dto.UserDto;
import com.revcart.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PagedResponse<UserDto> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.listAllUsers(pageable);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/role")
    public ApiResponse<UserDto> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        UserDto updated = userService.updateUserRole(id, request.role);
        return ApiResponse.<UserDto>builder()
                .success(true)
                .data(updated)
                .message("User role updated successfully")
                .build();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<UserDto> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        UserDto updated = userService.updateUserStatus(id, request.active);
        return ApiResponse.<UserDto>builder()
                .success(true)
                .data(updated)
                .message("User status updated successfully")
                .build();
    }

    private static class UpdateRoleRequest {
        public String role;
    }

    private static class UpdateStatusRequest {
        public boolean active;
    }
}

