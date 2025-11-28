package com.revcart.service;

import com.revcart.dto.AddressDto;
import com.revcart.dto.PagedResponse;
import com.revcart.dto.UserDto;
import com.revcart.dto.request.ProfileUpdateRequest;
import com.revcart.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto getCurrentUser();
    User findByEmail(String email);
    List<UserDto> findAllDeliveryAgents();
    List<AddressDto> getAddresses();
    AddressDto addAddress(AddressDto dto);
    AddressDto updateAddress(Long id, AddressDto dto);
    void deleteAddress(Long id);
    UserDto updateProfile(ProfileUpdateRequest request);
    void changePassword(com.revcart.dto.request.ChangePasswordRequest request);
    PagedResponse<UserDto> listAllUsers(Pageable pageable);
    UserDto getUserById(Long id);
    UserDto updateUserRole(Long id, String role);
    UserDto updateUserStatus(Long id, boolean active);
}

