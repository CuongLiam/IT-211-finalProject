package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LecturerSubmissionResponse {
    private Long submissionId;
    private Long assignmentId;
    private String assignmentTitle;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String githubUrl;
    private String fileUrl;
    private String originalFileName;
    private String status;
    private LocalDateTime submittedAt;
    private boolean graded;
}
