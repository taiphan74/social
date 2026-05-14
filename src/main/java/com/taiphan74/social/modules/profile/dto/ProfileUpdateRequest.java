package com.taiphan74.social.modules.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Profile update request")
public class ProfileUpdateRequest {

    @Size(max = 100)
    @Schema(description = "Display name", example = "John Doe")
    private String displayName;

    @Size(max = 500)
    @Schema(description = "Avatar URL")
    private String avatarUrl;

    @Size(max = 255)
    @Schema(description = "Short bio")
    private String bio;

    @Size(max = 20)
    @Schema(description = "Phone number")
    private String phoneNumber;

    @Schema(description = "Date of birth (YYYY-MM-DD)")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender", allowableValues = {"MALE", "FEMALE", "OTHER"})
    private String gender;
}
