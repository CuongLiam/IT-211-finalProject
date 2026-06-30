package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ForgotPasswordResponse {
    private String message;
    private String resetToken;
    private LocalDateTime expiresAt;
}