package com.taiphan74.social.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class LoginRequest {
    @NotBlank(message = "Username không được để trống")
    @Schema(description = "Username", example = "john_doe")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Schema(description = "Password", example = "P@ssw0rd")
    private String password;
}
