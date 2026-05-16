package com.taiphan74.social.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

import com.taiphan74.social.modules.user.entity.ERole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "User response data")
public class UserResponse {
    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @Schema(description = "Username", example = "john_doe")
    private String username;
    @Schema(description = "Email", example = "john@example.com")
    private String email;
    @Schema(description = "Role", example = "USER")
    private ERole role;
    @Schema(description = "Is account enabled")
    private boolean enabled;
    @Schema(description = "Is email verified")
    private boolean verified;
    @Schema(description = "Whether user has customized username")
    private boolean usernameCustomized;
    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
