package com.example.backend.repository;

import com.example.backend.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
	Page<Assignment> findByCourseLecturerId(Long lecturerId, Pageable pageable);

	Page<Assignment> findByCourseIdIn(List<Long> courseIds, Pageable pageable);
}
