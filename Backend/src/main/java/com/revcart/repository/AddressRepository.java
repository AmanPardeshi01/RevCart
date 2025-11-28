package com.revcart.repository;

import com.revcart.entity.Address;
import com.revcart.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}

