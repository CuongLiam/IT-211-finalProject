package com.example.backend.repository;

import com.example.backend.entity.LectureMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {
    Page<LectureMaterial> findByLecturerId(Long lecturerId, Pageable pageable);

    Page<LectureMaterial> findByCourseIdIn(java.util.List<Long> courseIds, Pageable pageable);
}
