package com.example.backend.service;

import com.example.backend.dto.request.CreateAssignmentRequest;
import com.example.backend.dto.response.AssignmentResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Assignment;
import com.example.backend.entity.Course;
import com.example.backend.entity.User;
import com.example.backend.entity.enums.EnrollmentStatus;
import com.example.backend.repository.AssignmentRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public AssignmentResponse createForLecturer(String lecturerEmail, CreateAssignmentRequest request) {
        User lecturer = findUserByEmail(lecturerEmail);
        Course course = findCourseById(request.getCourseId());

        if (!course.getLecturer().getId().equals(lecturer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create assignments for your courses");
        }

        if (request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due date must be in the future");
        }

        Assignment assignment = Assignment.builder()
                .course(course)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .maxScore(request.getMaxScore() == null ? BigDecimal.valueOf(10.00) : request.getMaxScore())
                .build();

        return toResponse(assignmentRepository.save(assignment));
    }

    @Transactional(readOnly = true)
    public PageResponse<AssignmentResponse> listForLecturer(String lecturerEmail, int page, int size) {
        User lecturer = findUserByEmail(lecturerEmail);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Assignment> assignmentPage = assignmentRepository.findByCourseLecturerId(lecturer.getId(), pageable);
        List<AssignmentResponse> content = assignmentPage.getContent().stream().map(this::toResponse).toList();

        return PageResponse.<AssignmentResponse>builder()
                .content(content)
                .page(assignmentPage.getNumber())
                .size(assignmentPage.getSize())
                .totalElements(assignmentPage.getTotalElements())
                .totalPages(assignmentPage.getTotalPages())
                .last(assignmentPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<AssignmentResponse> listForStudent(String studentEmail, int page, int size) {
        User student = findUserByEmail(studentEmail);

        List<Long> enrolledCourseIds = enrollmentRepository
                .findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ACTIVE, Pageable.unpaged())
                .getContent()
                .stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .toList();

        if (enrolledCourseIds.isEmpty()) {
            return PageResponse.<AssignmentResponse>builder()
                    .content(List.of())
                    .page(0)
                    .size(size)
                    .totalElements(0)
                    .totalPages(0)
                    .last(true)
                    .build();
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "dueDate"));
        Page<Assignment> assignmentPage = assignmentRepository.findByCourseIdIn(enrolledCourseIds, pageable);
        List<AssignmentResponse> content = assignmentPage.getContent().stream().map(this::toResponse).toList();

        return PageResponse.<AssignmentResponse>builder()
                .content(content)
                .page(assignmentPage.getNumber())
                .size(assignmentPage.getSize())
                .totalElements(assignmentPage.getTotalElements())
                .totalPages(assignmentPage.getTotalPages())
                .last(assignmentPage.isLast())
                .build();
    }

    private AssignmentResponse toResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .assignmentId(assignment.getId())
                .courseId(assignment.getCourse().getId())
                .courseCode(assignment.getCourse().getCode())
                .courseName(assignment.getCourse().getName())
                .lecturerId(assignment.getCourse().getLecturer().getId())
                .lecturerName(assignment.getCourse().getLecturer().getFullName())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .maxScore(assignment.getMaxScore())
                .createdAt(assignment.getCreatedAt())
                .build();
    }

    private User findUserByEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        return userRepository.findByEmail(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }
}
