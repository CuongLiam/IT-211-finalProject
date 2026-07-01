package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.EnrollmentCourseResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.StudentCourseResponse;
import com.example.backend.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/student/courses")
@RequiredArgsConstructor
public class StudentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StudentCourseResponse>>> listCourses(
            Authentication authentication,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<StudentCourseResponse> data = enrollmentService.listAvailableCourses(currentEmail(authentication), keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("List courses successful", data));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentCourseResponse>>> listMyEnrollments(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<EnrollmentCourseResponse> data = enrollmentService.listMyEnrollments(currentEmail(authentication), page, size);
        return ResponseEntity.ok(ApiResponse.success("List enrollments successful", data));
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<ApiResponse<EnrollmentCourseResponse>> enrollCourse(
            Authentication authentication,
            @PathVariable Long courseId) {
        EnrollmentCourseResponse data = enrollmentService.enrollCourse(currentEmail(authentication), courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Enroll course successful", data));
    }

    @DeleteMapping("/{courseId}/enroll")
    public ResponseEntity<Void> cancelEnrollment(
            Authentication authentication,
            @PathVariable Long courseId) {
        enrollmentService.cancelEnrollment(currentEmail(authentication), courseId);
        return ResponseEntity.noContent().build();
    }

    private String currentEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return authentication.getName();
    }
}