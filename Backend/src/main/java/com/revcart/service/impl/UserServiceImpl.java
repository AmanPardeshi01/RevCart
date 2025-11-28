package com.revcart.service.impl;

import com.revcart.dto.AddressDto;
import com.revcart.dto.PagedResponse;
import com.revcart.dto.UserDto;
import com.revcart.dto.request.ChangePasswordRequest;
import com.revcart.dto.request.ProfileUpdateRequest;
import com.revcart.entity.Address;
import com.revcart.entity.User;
import com.revcart.enums.UserRole;
import com.revcart.exception.BadRequestException;
import com.revcart.exception.ResourceNotFoundException;
import com.revcart.mapper.AddressMapper;
import com.revcart.mapper.UserMapper;
import com.revcart.repository.AddressRepository;
import com.revcart.repository.UserRepository;
import com.revcart.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AddressRepository addressRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto getCurrentUser() {
        return UserMapper.toDto(getAuthenticatedUser());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<UserDto> findAllDeliveryAgents() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.DELIVERY_AGENT)
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddressDto> getAddresses() {
        User user = getAuthenticatedUser();
        return addressRepository.findByUser(user).stream()
                .map(AddressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDto addAddress(AddressDto dto) {
        User user = getAuthenticatedUser();
        // If this is set as primary, unset other primary addresses
        if (dto.isPrimaryAddress()) {
            addressRepository.findByUser(user).forEach(addr -> addr.setPrimaryAddress(false));
        }
        Address address = new Address();
        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setPrimaryAddress(dto.isPrimaryAddress());
        address.setUser(user);
        Address saved = addressRepository.save(address);
        return AddressMapper.toDto(saved);
    }

    @Override
    public AddressDto updateAddress(Long id, AddressDto dto) {
        User user = getAuthenticatedUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only update your own addresses");
        }
        // If this is set as primary, unset other primary addresses
        if (dto.isPrimaryAddress()) {
            addressRepository.findByUser(user).stream()
                    .filter(addr -> !addr.getId().equals(id))
                    .forEach(addr -> addr.setPrimaryAddress(false));
        }
        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setPrimaryAddress(dto.isPrimaryAddress());
        Address saved = addressRepository.save(address);
        return AddressMapper.toDto(saved);
    }

    @Override
    public void deleteAddress(Long id) {
        User user = getAuthenticatedUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only delete your own addresses");
        }
        addressRepository.delete(address);
    }

    @Override
    public UserDto updateProfile(ProfileUpdateRequest request) {
        User user = getAuthenticatedUser();
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(request.getAvatarUrl());
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public PagedResponse<UserDto> listAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        List<UserDto> content = page.getContent().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
        return PagedResponse.<UserDto>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        try {
            UserRole newRole = UserRole.valueOf(role.toUpperCase());
            user.setRole(newRole);
            return UserMapper.toDto(userRepository.save(user));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role);
        }
    }

    @Override
    public UserDto updateUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(active);
        return UserMapper.toDto(userRepository.save(user));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ResourceNotFoundException("No authenticated user");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}

