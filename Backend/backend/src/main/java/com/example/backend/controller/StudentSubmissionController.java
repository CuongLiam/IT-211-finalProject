package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.SubmissionResponse;
import com.example.backend.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/student/submissions")
@RequiredArgsConstructor
@Tag(name = "Student Submission", description = "Submit assignment and view submission history")
public class StudentSubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "Submit assignment", description = "Submit GitHub link and document file (PDF/Word/Slide up to 15MB)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitAssignment(
            Authentication authentication,
            @RequestParam Long assignmentId,
            @RequestParam String githubUrl,
            @RequestPart MultipartFile file) {
        SubmissionResponse data = submissionService.submitAssignment(currentEmail(authentication), assignmentId, githubUrl, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Submit assignment successful", data));
    }

    @Operation(summary = "List my submissions", description = "List student submissions with pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> listMySubmissions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SubmissionResponse> data = submissionService.listMySubmissions(currentEmail(authentication), page, size);
        return ResponseEntity.ok(ApiResponse.success("List submissions successful", data));
    }

    private String currentEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return authentication.getName();
    }
}
