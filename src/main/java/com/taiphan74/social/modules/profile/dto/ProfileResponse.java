package com.taiphan74.social.modules.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "User profile response")
public class ProfileResponse {
    private UUID id;
    private UUID userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
