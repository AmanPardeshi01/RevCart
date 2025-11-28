package com.revcart.mapper;

import com.revcart.dto.AddressDto;
import com.revcart.entity.Address;

public final class AddressMapper {

    private AddressMapper() {
    }

    public static AddressDto toDto(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDto.builder()
                .id(address.getId())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .primaryAddress(address.isPrimaryAddress())
                .build();
    }
}

