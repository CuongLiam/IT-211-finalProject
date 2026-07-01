package com.example.backend.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE_BYTES = 15L * 1024 * 1024;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    );

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            ".pdf",
            ".doc",
            ".docx",
            ".ppt",
            ".pptx"
    );

    public void validateDocumentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size must be <= 15MB");
        }

        String filename = file.getOriginalFilename();
        if (!hasAllowedExtension(filename)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type. Only PDF/Word/Slide are allowed");
        }

        String contentType = file.getContentType();
        if (contentType == null || ALLOWED_CONTENT_TYPES.stream().noneMatch(type -> type.equalsIgnoreCase(contentType))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid content type for uploaded file");
        }
    }

    private boolean hasAllowedExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }

        String lowered = filename.toLowerCase();
        return ALLOWED_EXTENSIONS.stream().anyMatch(lowered::endsWith);
    }
}
