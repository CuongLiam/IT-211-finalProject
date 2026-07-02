package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentResponse {
    private Long assignmentId;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long lecturerId;
    private String lecturerName;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private BigDecimal maxScore;
    private LocalDateTime createdAt;
}
