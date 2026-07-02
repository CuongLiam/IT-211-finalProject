package com.example.backend.service;

import com.example.backend.entity.TokenBlacklist;
import com.example.backend.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Token blacklist lưu trong MySQL (JPA) thay vì Redis.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    /**
     * Thêm token vào blacklist trong DB.
     *
     * @param token     JWT token cần blacklist
     * @param expiredAt thời điểm token hết hạn
     */
    @Transactional
    public void blacklistToken(String token, LocalDateTime expiredAt) {
        if (token == null || token.isBlank()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        tokenBlacklistRepository.deleteByExpiredAtBefore(now);

        if (expiredAt == null || !expiredAt.isAfter(now)) {
            log.debug("Token already expired, skipping blacklist persist");
            return;
        }

        if (tokenBlacklistRepository.existsByToken(token)) {
            return;
        }

        TokenBlacklist entry = TokenBlacklist.builder()
                .token(token)
                .expiredAt(expiredAt)
                .build();

        tokenBlacklistRepository.save(entry);
        log.info("Token blacklisted in MySQL. expiresAt={}", expiredAt);
    }

    /**
     * Kiểm tra token có nằm trong blacklist hay không.
     *
     * @param token JWT token cần kiểm tra
     * @return true nếu token đã bị revoke
     */
    @Transactional
    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        Optional<TokenBlacklist> found = tokenBlacklistRepository.findByToken(token);
        if (found.isEmpty()) {
            return false;
        }

        TokenBlacklist entry = found.get();
        if (entry.getExpiredAt() != null && entry.getExpiredAt().isBefore(LocalDateTime.now())) {
            tokenBlacklistRepository.delete(entry);
            return false;
        }

        return true;
    }
}
