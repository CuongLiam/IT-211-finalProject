package com.example.backend.repository;

import com.example.backend.entity.PasswordResetToken;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    void deleteByExpiresAtBefore(LocalDateTime now);
    void deleteByUserAndUsedFalse(User user);
}