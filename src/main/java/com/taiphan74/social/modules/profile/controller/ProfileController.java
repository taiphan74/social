package com.taiphan74.social.modules.profile.controller;

import com.taiphan74.social.modules.profile.dto.ProfileResponse;
import com.taiphan74.social.modules.profile.dto.ProfileUpdateRequest;
import com.taiphan74.social.modules.profile.service.IProfileService;
import com.taiphan74.social.modules.user.entity.User;
import com.taiphan74.social.modules.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "User Profiles", description = "User profile management")
public class ProfileController {

    private final IProfileService profileService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get my profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile found",
                     content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        return ResponseEntity.ok(profileService.getByUserId(userId));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create or update my profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile upserted",
                     content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
        @ApiResponse(responseCode = "422", description = "Validation error")
    })
    public ResponseEntity<ProfileResponse> upsertMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProfileUpdateRequest request) {
        UUID userId = getUserId(userDetails);
        return ResponseEntity.ok(profileService.upsert(userId, request));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get profile by user ID (admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile found",
                     content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfileResponse> getProfileByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getByUserId(userId));
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
        return user.getId();
    }
}
