package com.example.backend.dto.request;

import com.example.backend.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be <= 120 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    @Size(max = 160, message = "Email must be <= 160 characters")
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    @NotNull(message = "Enabled is required")
    private Boolean enabled;
}