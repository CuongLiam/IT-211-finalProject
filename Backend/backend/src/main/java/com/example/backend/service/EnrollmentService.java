package com.example.backend.service;

import com.example.backend.dto.response.EnrollmentCourseResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.StudentCourseResponse;
import com.example.backend.entity.Course;
import com.example.backend.entity.Enrollment;
import com.example.backend.entity.User;
import com.example.backend.entity.enums.EnrollmentStatus;
import com.example.backend.repository.CourseRepository;
import com.example.backend.repository.EnrollmentRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<StudentCourseResponse> listAvailableCourses(String authenticatedEmail, String keyword, int page, int size) {
        User student = findUserByEmail(authenticatedEmail);

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.ASC, "name"));
        String kw = keyword == null ? "" : keyword.trim();

        Page<Course> coursePage = courseRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(kw, kw, pageable);

        List<StudentCourseResponse> content = coursePage.getContent()
                .stream()
                .map(course -> StudentCourseResponse.builder()
                        .id(course.getId())
                        .code(course.getCode())
                        .name(course.getName())
                        .description(course.getDescription())
                        .lecturerId(course.getLecturer().getId())
                        .lecturerName(course.getLecturer().getFullName())
                        .lecturerEmail(course.getLecturer().getEmail())
                        .enrolled(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                                course.getId(),
                                student.getId(),
                                EnrollmentStatus.ACTIVE
                        ))
                        .build())
                .toList();

        return PageResponse.<StudentCourseResponse>builder()
                .content(content)
                .page(coursePage.getNumber())
                .size(coursePage.getSize())
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .last(coursePage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<EnrollmentCourseResponse> listMyEnrollments(String authenticatedEmail, int page, int size) {
        User student = findUserByEmail(authenticatedEmail);

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "enrolledAt"));
        Page<Enrollment> enrollmentPage = enrollmentRepository.findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ACTIVE, pageable);

        List<EnrollmentCourseResponse> content = enrollmentPage.getContent()
                .stream()
                .map(this::mapEnrollmentToResponse)
                .toList();

        return PageResponse.<EnrollmentCourseResponse>builder()
                .content(content)
                .page(enrollmentPage.getNumber())
                .size(enrollmentPage.getSize())
                .totalElements(enrollmentPage.getTotalElements())
                .totalPages(enrollmentPage.getTotalPages())
                .last(enrollmentPage.isLast())
                .build();
    }

    @Transactional
    public EnrollmentCourseResponse enrollCourse(String authenticatedEmail, Long courseId) {
        User student = findUserByEmail(authenticatedEmail);
        Course course = findCourseById(courseId);

        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(course.getId(), student.getId())
                .map(existing -> {
                    if (existing.getStatus() == EnrollmentStatus.ACTIVE) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "You already enrolled in this course");
                    }
                    existing.setStatus(EnrollmentStatus.ACTIVE);
                    return existing;
                })
                .orElseGet(() -> Enrollment.builder()
                        .course(course)
                        .student(student)
                        .status(EnrollmentStatus.ACTIVE)
                        .build());

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapEnrollmentToResponse(saved);
    }

    @Transactional
    public void cancelEnrollment(String authenticatedEmail, Long courseId) {
        User student = findUserByEmail(authenticatedEmail);

        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, student.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Enrollment already cancelled");
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    private EnrollmentCourseResponse mapEnrollmentToResponse(Enrollment enrollment) {
        Course course = enrollment.getCourse();

        return EnrollmentCourseResponse.builder()
                .enrollmentId(enrollment.getId())
                .courseId(course.getId())
                .courseCode(course.getCode())
                .courseName(course.getName())
                .courseDescription(course.getDescription())
                .lecturerId(course.getLecturer().getId())
                .lecturerName(course.getLecturer().getFullName())
                .status(enrollment.getStatus().name())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase();
    }
}