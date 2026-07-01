package com.example.backend.controller;

import com.example.backend.dto.request.*;
import com.example.backend.dto.response.AdminCourseResponse;
import com.example.backend.dto.response.AdminUserResponse;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.enums.Role;
import com.example.backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<AdminUserResponse> data = adminService.searchUsers(keyword, role, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Search users successful", data));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        AdminUserResponse data = adminService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Create user successful", data));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(@PathVariable Long id) {
        AdminUserResponse data = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Get user successful", data));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        AdminUserResponse data = adminService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("Update user successful", data));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<AdminCourseResponse>>> searchCourses(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<AdminCourseResponse> data = adminService.searchCourses(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Search courses successful", data));
    }

    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<AdminCourseResponse>> createCourse(@Valid @RequestBody AdminCreateCourseRequest request) {
        AdminCourseResponse data = adminService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Create course successful", data));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<AdminCourseResponse>> getCourseById(@PathVariable Long id) {
        AdminCourseResponse data = adminService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success("Get course successful", data));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<AdminCourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateCourseRequest request) {
        AdminCourseResponse data = adminService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success("Update course successful", data));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        adminService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}