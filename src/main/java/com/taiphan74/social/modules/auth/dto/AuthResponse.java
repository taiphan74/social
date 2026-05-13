package com.taiphan74.social.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.taiphan74.social.modules.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Authentication response")
public class AuthResponse {
    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIs...")
    private String token;
    @Schema(description = "Authenticated user info")
    private UserResponse user;
}
