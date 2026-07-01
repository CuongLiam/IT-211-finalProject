package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminCourseResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long lecturerId;
    private String lecturerName;
    private String lecturerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}