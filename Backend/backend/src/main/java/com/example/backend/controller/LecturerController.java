package com.example.backend.controller;

import com.example.backend.dto.request.CreateAssignmentRequest;
import com.example.backend.dto.request.GradeRequest;
import com.example.backend.dto.response.*;
import com.example.backend.service.AssignmentService;
import com.example.backend.service.GradeService;
import com.example.backend.service.LectureMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
@Tag(name = "Lecturer", description = "Grading and lecture material management")
public class LecturerController {

    private final AssignmentService assignmentService;
    private final GradeService gradeService;
    private final LectureMaterialService lectureMaterialService;

    @Operation(summary = "Create assignment for lecturer course")
    @PostMapping("/assignments")
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
            Authentication authentication,
            @Valid @RequestBody CreateAssignmentRequest request) {
        AssignmentResponse data = assignmentService.createForLecturer(currentEmail(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Create assignment successful", data));
    }

    @Operation(summary = "List lecturer assignments")
    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<PageResponse<AssignmentResponse>>> listLecturerAssignments(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AssignmentResponse> data = assignmentService.listForLecturer(currentEmail(authentication), page, size);
        return ResponseEntity.ok(ApiResponse.success("List assignments successful", data));
    }

    @Operation(summary = "List submissions for grading")
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<PageResponse<LecturerSubmissionResponse>>> listSubmissions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<LecturerSubmissionResponse> data = gradeService.listSubmissionsForLecturer(currentEmail(authentication), page, size);
        return ResponseEntity.ok(ApiResponse.success("List submissions successful", data));
    }

    @Operation(summary = "Grade submission")
    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<ApiResponse<GradeResponse>> gradeSubmission(
            Authentication authentication,
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeRequest request) {
        GradeResponse data = gradeService.gradeSubmission(currentEmail(authentication), submissionId, request);
        return ResponseEntity.ok(ApiResponse.success("Grade submission successful", data));
    }

    @Operation(summary = "List lecturer grade summary")
    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<PageResponse<GradeResponse>>> listLecturerGrades(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<GradeResponse> data = gradeService.listLecturerGrades(currentEmail(authentication), page, size);
        return ResponseEntity.ok(ApiResponse.success("List grades successful", data));
    }

    @Operation(summary = "Upload lecture material")
    @PostMapping(value = "/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LectureMaterialResponse>> uploadMaterial(
            Authentication authentication,
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestPart MultipartFile file) {
        LectureMaterialResponse data = lectureMaterialService.uploadMaterial(currentEmail(authentication), courseId, title, description, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Upload material successful", data));
    }

    @Operation(summary = "List lecturer materials")
    @GetMapping("/materials")
    public ResponseEntity<ApiResponse<PageResponse<LectureMaterialResponse>>> listLecturerMaterials(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<LectureMaterialResponse> data = lectureMaterialService.listLecturerMaterials(currentEmail(authentication), page, size);
        return ResponseEntity.ok(ApiResponse.success("List materials successful", data));
    }

    @Operation(summary = "Update lecture material")
    @PutMapping("/materials/{materialId}")
    public ResponseEntity<ApiResponse<LectureMaterialResponse>> updateMaterial(
            Authentication authentication,
            @PathVariable Long materialId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {
        LectureMaterialResponse data = lectureMaterialService.updateMaterial(currentEmail(authentication), materialId, title, description);
        return ResponseEntity.ok(ApiResponse.success("Update material successful", data));
    }

    @Operation(summary = "Delete lecture material")
    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(
            Authentication authentication,
            @PathVariable Long materialId) {
        lectureMaterialService.deleteMaterial(currentEmail(authentication), materialId);
        return ResponseEntity.noContent().build();
    }

    private String currentEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return authentication.getName();
    }
}
