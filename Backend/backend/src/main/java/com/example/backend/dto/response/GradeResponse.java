package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class GradeResponse {
    private Long gradeId;
    private Long submissionId;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private BigDecimal score;
    private String feedback;
    private LocalDateTime gradedAt;
}
