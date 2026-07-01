package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentCourseResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String courseDescription;
    private Long lecturerId;
    private String lecturerName;
    private String status;
    private LocalDateTime enrolledAt;
}