package com.example.backend.controller;

import com.example.backend.dto.response.*;
import com.example.backend.service.GradeService;
import com.example.backend.service.LectureMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "Student Learning", description = "Student grade and lecture material endpoints")
public class StudentMaterialAndGradeController {

    private final GradeService gradeService;
    private final LectureMaterialService lectureMaterialService;

    @Operation(summary = "List my grades")
    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<PageResponse<GradeResponse>>> listMyGrades(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<GradeResponse> data = gradeService.listStudentGrades(currentEmail(authentication), page, size);
        return ResponseEntity.ok(ApiResponse.success("List grades successful", data));
    }

    @Operation(summary = "List learning materials of enrolled courses")
    @GetMapping("/materials")
    public ResponseEntity<ApiResponse<PageResponse<LectureMaterialResponse>>> listStudentMaterials(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<LectureMaterialResponse> data = lectureMaterialService.listStudentMaterials(currentEmail(authentication), page, size);
        return ResponseEntity.ok(ApiResponse.success("List materials successful", data));
    }

    private String currentEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return authentication.getName();
    }
}
