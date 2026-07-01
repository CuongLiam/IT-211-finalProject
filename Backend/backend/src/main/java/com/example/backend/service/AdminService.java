package com.example.backend.service;

import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.entity.Course;
import com.example.backend.entity.User;
import com.example.backend.entity.enums.Role;
import com.example.backend.repository.CourseRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> searchUsers(String keyword, Role role, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);

        String kw = normalizeKeyword(keyword);
        Page<User> userPage;
        if (role == null) {
            userPage = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(kw, kw, pageable);
        } else {
            userPage = userRepository.findByRoleAndFullNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
                    role, kw, role, kw, pageable
            );
        }

        // Stream API map Entity -> DTO
        List<AdminUserResponse> content = userPage.getContent()
                .stream()
                .map(this::mapUserToResponse)
                .toList();

        return PageResponse.<AdminUserResponse>builder()
                .content(content)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    @Transactional
    public AdminUserResponse createUser(AdminCreateUserRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(request.getEnabled())
                .build();

        return mapUserToResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(Long id) {
        return mapUserToResponse(findUserById(id));
    }

    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUpdateUserRequest request) {
        User user = findUserById(id);
        String normalizedEmail = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setRole(request.getRole());
        user.setEnabled(request.getEnabled());

        return mapUserToResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminCourseResponse> searchCourses(String keyword, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        String kw = normalizeKeyword(keyword);

        Page<Course> coursePage = courseRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(kw, kw, pageable);

        // Stream API map Entity -> DTO
        List<AdminCourseResponse> content = coursePage.getContent()
                .stream()
                .map(this::mapCourseToResponse)
                .toList();

        return PageResponse.<AdminCourseResponse>builder()
                .content(content)
                .page(coursePage.getNumber())
                .size(coursePage.getSize())
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .last(coursePage.isLast())
                .build();
    }

    @Transactional
    public AdminCourseResponse createCourse(AdminCreateCourseRequest request) {
        String code = request.getCode().trim().toUpperCase();

        if (courseRepository.existsByCodeIgnoreCase(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course code already exists");
        }

        User lecturer = findLecturerById(request.getLecturerId());

        Course course = Course.builder()
                .code(code)
                .name(request.getName().trim())
                .description(request.getDescription())
                .lecturer(lecturer)
                .build();

        return mapCourseToResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public AdminCourseResponse getCourseById(Long id) {
        return mapCourseToResponse(findCourseById(id));
    }

    @Transactional
    public AdminCourseResponse updateCourse(Long id, AdminUpdateCourseRequest request) {
        Course course = findCourseById(id);
        String code = request.getCode().trim().toUpperCase();

        if (!course.getCode().equalsIgnoreCase(code) && courseRepository.existsByCodeIgnoreCase(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course code already exists");
        }

        User lecturer = findLecturerById(request.getLecturerId());

        course.setCode(code);
        course.setName(request.getName().trim());
        course.setDescription(request.getDescription());
        course.setLecturer(lecturer);

        return mapCourseToResponse(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = findCourseById(id);
        courseRepository.delete(course);
    }

    private AdminUserResponse mapUserToResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private AdminCourseResponse mapCourseToResponse(Course course) {
        return AdminCourseResponse.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .description(course.getDescription())
                .lecturerId(course.getLecturer().getId())
                .lecturerName(course.getLecturer().getFullName())
                .lecturerEmail(course.getLecturer().getEmail())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    private User findLecturerById(Long lecturerId) {
        User lecturer = findUserById(lecturerId);
        if (lecturer.getRole() != Role.LECTURER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided user is not a lecturer");
        }
        return lecturer;
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, safeSortBy));
    }
}