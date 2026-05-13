package com.taiphan74.social.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "User creation request")
public class UserCreateRequest {
    @NotBlank
    @Size(max = 50)
    @Schema(description = "Username", example = "john_doe")
    private String username;

    @NotBlank
    @Size(min = 6)
    @Schema(description = "Password (at least 6 characters)", example = "P@ssw0rd")
    private String password;

    @NotBlank
    @Email
    @Size(max = 100)
    @Schema(description = "Email address", example = "john@example.com")
    private String email;
}
