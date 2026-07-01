package com.example.backend.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${app.cloudinary.folder}")
    private String folder;

    public String uploadDocument(MultipartFile file) {
        try {
            String publicId = buildPublicId(file.getOriginalFilename());

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", folder,
                            "public_id", publicId,
                            "overwrite", true
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cloud upload failed");
            }

            return secureUrl.toString();
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cannot read file for upload");
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cloud storage unavailable");
        }
    }

    private String buildPublicId(String originalFilename) {
        String safeName = originalFilename == null ? "document" : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return UUID.randomUUID() + "_" + safeName;
    }
}
