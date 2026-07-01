package com.example.backend.service;

import com.example.backend.dto.request.GradeRequest;
import com.example.backend.dto.response.GradeResponse;
import com.example.backend.dto.response.LecturerSubmissionResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Grade;
import com.example.backend.entity.Submission;
import com.example.backend.entity.User;
import com.example.backend.repository.GradeRepository;
import com.example.backend.repository.SubmissionRepository;
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
public class GradeService {

    private final GradeRepository gradeRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<LecturerSubmissionResponse> listSubmissionsForLecturer(String lecturerEmail, int page, int size) {
        User lecturer = findUserByEmail(lecturerEmail);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "submittedAt"));

        Page<Submission> submissionPage = submissionRepository.findByAssignmentCourseLecturerId(lecturer.getId(), pageable);
        List<LecturerSubmissionResponse> content = submissionPage.getContent().stream().map(this::toLecturerSubmission).toList();

        return PageResponse.<LecturerSubmissionResponse>builder()
                .content(content)
                .page(submissionPage.getNumber())
                .size(submissionPage.getSize())
                .totalElements(submissionPage.getTotalElements())
                .totalPages(submissionPage.getTotalPages())
                .last(submissionPage.isLast())
                .build();
    }

    @Transactional
    public GradeResponse gradeSubmission(String lecturerEmail, Long submissionId, GradeRequest request) {
        User lecturer = findUserByEmail(lecturerEmail);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        if (!submission.getAssignment().getCourse().getLecturer().getId().equals(lecturer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only grade submissions in your course");
        }

        Grade grade = gradeRepository.findBySubmissionId(submissionId)
                .map(existing -> {
                    existing.setScore(request.getScore());
                    existing.setFeedback(request.getFeedback());
                    return existing;
                })
                .orElseGet(() -> Grade.builder()
                        .submission(submission)
                        .lecturer(lecturer)
                        .score(request.getScore())
                        .feedback(request.getFeedback())
                        .build());

        Grade saved = gradeRepository.save(grade);
        return toGradeResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<GradeResponse> listLecturerGrades(String lecturerEmail, int page, int size) {
        User lecturer = findUserByEmail(lecturerEmail);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "gradedAt"));

        Page<Grade> gradePage = gradeRepository.findByLecturerId(lecturer.getId(), pageable);
        List<GradeResponse> content = gradePage.getContent().stream().map(this::toGradeResponse).toList();

        return PageResponse.<GradeResponse>builder()
                .content(content)
                .page(gradePage.getNumber())
                .size(gradePage.getSize())
                .totalElements(gradePage.getTotalElements())
                .totalPages(gradePage.getTotalPages())
                .last(gradePage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<GradeResponse> listStudentGrades(String studentEmail, int page, int size) {
        User student = findUserByEmail(studentEmail);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "gradedAt"));

        Page<Grade> gradePage = gradeRepository.findBySubmissionStudentId(student.getId(), pageable);
        List<GradeResponse> content = gradePage.getContent().stream().map(this::toGradeResponse).toList();

        return PageResponse.<GradeResponse>builder()
                .content(content)
                .page(gradePage.getNumber())
                .size(gradePage.getSize())
                .totalElements(gradePage.getTotalElements())
                .totalPages(gradePage.getTotalPages())
                .last(gradePage.isLast())
                .build();
    }

    private LecturerSubmissionResponse toLecturerSubmission(Submission submission) {
        boolean graded = gradeRepository.findBySubmissionId(submission.getId()).isPresent();

        return LecturerSubmissionResponse.builder()
                .submissionId(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .assignmentTitle(submission.getAssignment().getTitle())
                .courseId(submission.getAssignment().getCourse().getId())
                .courseCode(submission.getAssignment().getCourse().getCode())
                .courseName(submission.getAssignment().getCourse().getName())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getFullName())
                .studentEmail(submission.getStudent().getEmail())
                .githubUrl(submission.getGithubUrl())
                .fileUrl(submission.getFileUrl())
                .originalFileName(submission.getOriginalFileName())
                .status(submission.getStatus().name())
                .submittedAt(submission.getSubmittedAt())
                .graded(graded)
                .build();
    }

    private GradeResponse toGradeResponse(Grade grade) {
        return GradeResponse.builder()
                .gradeId(grade.getId())
                .submissionId(grade.getSubmission().getId())
                .assignmentId(grade.getSubmission().getAssignment().getId())
                .assignmentTitle(grade.getSubmission().getAssignment().getTitle())
                .studentId(grade.getSubmission().getStudent().getId())
                .studentName(grade.getSubmission().getStudent().getFullName())
                .studentEmail(grade.getSubmission().getStudent().getEmail())
                .score(grade.getScore())
                .feedback(grade.getFeedback())
                .gradedAt(grade.getGradedAt())
                .build();
    }

    private User findUserByEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        return userRepository.findByEmail(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
