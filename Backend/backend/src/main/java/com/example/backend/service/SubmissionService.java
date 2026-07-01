package com.example.backend.service;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.SubmissionResponse;
import com.example.backend.entity.Assignment;
import com.example.backend.entity.Submission;
import com.example.backend.entity.User;
import com.example.backend.entity.enums.EnrollmentStatus;
import com.example.backend.entity.enums.SubmissionStatus;
import com.example.backend.repository.AssignmentRepository;
import com.example.backend.repository.EnrollmentRepository;
import com.example.backend.repository.SubmissionRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.CloudinaryService;
import com.example.backend.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final FileValidator fileValidator;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public SubmissionResponse submitAssignment(String studentEmail, Long assignmentId, String githubUrl, MultipartFile file) {
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GitHub URL is required");
        }

        if (!githubUrl.startsWith("http://") && !githubUrl.startsWith("https://")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GitHub URL is invalid");
        }

        User student = findUserByEmail(studentEmail);
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        boolean enrolled = enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                assignment.getCourse().getId(),
                student.getId(),
                EnrollmentStatus.ACTIVE
        );

        if (!enrolled) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must enroll this course before submitting");
        }

        fileValidator.validateDocumentFile(file);
        String fileUrl = cloudinaryService.uploadDocument(file);

        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, student.getId())
                .map(existing -> {
                    existing.setGithubUrl(githubUrl.trim());
                    existing.setFileUrl(fileUrl);
                    existing.setOriginalFileName(file.getOriginalFilename());
                    existing.setStatus(SubmissionStatus.RESUBMITTED);
                    return existing;
                })
                .orElseGet(() -> Submission.builder()
                        .assignment(assignment)
                        .student(student)
                        .githubUrl(githubUrl.trim())
                        .fileUrl(fileUrl)
                        .originalFileName(file.getOriginalFilename())
                        .status(SubmissionStatus.SUBMITTED)
                        .build());

        Submission saved = submissionRepository.save(submission);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<SubmissionResponse> listMySubmissions(String studentEmail, int page, int size) {
        User student = findUserByEmail(studentEmail);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "submittedAt"));

        Page<Submission> submissionPage = submissionRepository.findByStudentId(student.getId(), pageable);

        List<SubmissionResponse> content = submissionPage.getContent().stream().map(this::toResponse).toList();

        return PageResponse.<SubmissionResponse>builder()
                .content(content)
                .page(submissionPage.getNumber())
                .size(submissionPage.getSize())
                .totalElements(submissionPage.getTotalElements())
                .totalPages(submissionPage.getTotalPages())
                .last(submissionPage.isLast())
                .build();
    }

    private SubmissionResponse toResponse(Submission submission) {
        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .assignmentTitle(submission.getAssignment().getTitle())
                .courseId(submission.getAssignment().getCourse().getId())
                .courseCode(submission.getAssignment().getCourse().getCode())
                .courseName(submission.getAssignment().getCourse().getName())
                .githubUrl(submission.getGithubUrl())
                .fileUrl(submission.getFileUrl())
                .originalFileName(submission.getOriginalFileName())
                .status(submission.getStatus().name())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }

    private User findUserByEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        return userRepository.findByEmail(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
