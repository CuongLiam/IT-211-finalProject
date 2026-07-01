package com.example.backend.repository;

import com.example.backend.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findBySubmissionId(Long submissionId);

    Page<Grade> findByLecturerId(Long lecturerId, Pageable pageable);

    Page<Grade> findBySubmissionStudentId(Long studentId, Pageable pageable);
}
