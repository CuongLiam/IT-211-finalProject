package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminCreateCourseRequest {

    @NotBlank(message = "Course code is required")
    @Size(max = 30, message = "Course code must be <= 30 characters")
    private String code;

    @NotBlank(message = "Course name is required")
    @Size(max = 150, message = "Course name must be <= 150 characters")
    private String name;

    private String description;

    @NotNull(message = "Lecturer id is required")
    private Long lecturerId;
}