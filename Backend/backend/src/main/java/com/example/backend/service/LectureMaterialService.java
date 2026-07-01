package com.example.backend.service;

import com.example.backend.dto.response.LectureMaterialResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Course;
import com.example.backend.entity.LectureMaterial;
import com.example.backend.entity.User;
import com.example.backend.entity.enums.EnrollmentStatus;
import com.example.backend.repository.CourseRepository;
import com.example.backend.repository.EnrollmentRepository;
import com.example.backend.repository.LectureMaterialRepository;
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
public class LectureMaterialService {

    private final LectureMaterialRepository lectureMaterialRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final FileValidator fileValidator;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public LectureMaterialResponse uploadMaterial(String lecturerEmail, Long courseId, String title, String description, MultipartFile file) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }

        User lecturer = findUserByEmail(lecturerEmail);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!course.getLecturer().getId().equals(lecturer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only upload material to your course");
        }

        fileValidator.validateDocumentFile(file);
        String fileUrl = cloudinaryService.uploadDocument(file);

        LectureMaterial material = LectureMaterial.builder()
                .course(course)
                .lecturer(lecturer)
                .title(title.trim())
                .description(description)
                .fileUrl(fileUrl)
                .originalFileName(file.getOriginalFilename())
                .build();

        LectureMaterial saved = lectureMaterialRepository.save(material);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<LectureMaterialResponse> listLecturerMaterials(String lecturerEmail, int page, int size) {
        User lecturer = findUserByEmail(lecturerEmail);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "uploadedAt"));

        Page<LectureMaterial> materialPage = lectureMaterialRepository.findByLecturerId(lecturer.getId(), pageable);
        List<LectureMaterialResponse> content = materialPage.getContent().stream().map(this::toResponse).toList();

        return PageResponse.<LectureMaterialResponse>builder()
                .content(content)
                .page(materialPage.getNumber())
                .size(materialPage.getSize())
                .totalElements(materialPage.getTotalElements())
                .totalPages(materialPage.getTotalPages())
                .last(materialPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<LectureMaterialResponse> listStudentMaterials(String studentEmail, int page, int size) {
        User student = findUserByEmail(studentEmail);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "uploadedAt"));

        List<Long> enrolledCourseIds = enrollmentRepository.findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ACTIVE, Pageable.unpaged())
                .getContent()
                .stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .toList();

        if (enrolledCourseIds.isEmpty()) {
            return PageResponse.<LectureMaterialResponse>builder()
                    .content(List.of())
                    .page(0)
                    .size(size)
                    .totalElements(0)
                    .totalPages(0)
                    .last(true)
                    .build();
        }

        Page<LectureMaterial> materialPage = lectureMaterialRepository.findByCourseIdIn(enrolledCourseIds, pageable);
        List<LectureMaterialResponse> content = materialPage.getContent().stream().map(this::toResponse).toList();

        return PageResponse.<LectureMaterialResponse>builder()
                .content(content)
                .page(materialPage.getNumber())
                .size(materialPage.getSize())
                .totalElements(materialPage.getTotalElements())
                .totalPages(materialPage.getTotalPages())
                .last(materialPage.isLast())
                .build();
    }

    @Transactional
    public LectureMaterialResponse updateMaterial(String lecturerEmail, Long materialId, String title, String description) {
        User lecturer = findUserByEmail(lecturerEmail);
        LectureMaterial material = lectureMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        if (!material.getLecturer().getId().equals(lecturer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your material");
        }

        if (title != null && !title.isBlank()) {
            material.setTitle(title.trim());
        }
        material.setDescription(description);

        return toResponse(lectureMaterialRepository.save(material));
    }

    @Transactional
    public void deleteMaterial(String lecturerEmail, Long materialId) {
        User lecturer = findUserByEmail(lecturerEmail);
        LectureMaterial material = lectureMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        if (!material.getLecturer().getId().equals(lecturer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your material");
        }

        lectureMaterialRepository.delete(material);
    }

    private LectureMaterialResponse toResponse(LectureMaterial material) {
        return LectureMaterialResponse.builder()
                .id(material.getId())
                .courseId(material.getCourse().getId())
                .courseCode(material.getCourse().getCode())
                .courseName(material.getCourse().getName())
                .lecturerId(material.getLecturer().getId())
                .lecturerName(material.getLecturer().getFullName())
                .title(material.getTitle())
                .description(material.getDescription())
                .fileUrl(material.getFileUrl())
                .originalFileName(material.getOriginalFileName())
                .uploadedAt(material.getUploadedAt())
                .updatedAt(material.getUpdatedAt())
                .build();
    }

    private User findUserByEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        return userRepository.findByEmail(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
