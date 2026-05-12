package com.taiphan74.social.modules.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taiphan74.social.modules.user.entity.ERole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private ERole role;
    private boolean enabled;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
