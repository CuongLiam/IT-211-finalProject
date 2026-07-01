package com.example.backend.repository;

import com.example.backend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCodeIgnoreCase(String code);
    Page<Course> findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(String codeKeyword, String nameKeyword, Pageable pageable);
}