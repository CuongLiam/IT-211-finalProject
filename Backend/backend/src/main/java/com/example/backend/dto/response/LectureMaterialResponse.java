package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LectureMaterialResponse {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long lecturerId;
    private String lecturerName;
    private String title;
    private String description;
    private String fileUrl;
    private String originalFileName;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
}
