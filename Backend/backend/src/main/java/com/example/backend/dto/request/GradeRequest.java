package com.example.backend.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradeRequest {

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Score must be >= 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Score must be <= 10")
    private BigDecimal score;

    private String feedback;
}
