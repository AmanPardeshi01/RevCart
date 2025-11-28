package com.revcart.repository;

import com.revcart.entity.OtpToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByEmailOrderByCreatedAtDesc(String email);
}

