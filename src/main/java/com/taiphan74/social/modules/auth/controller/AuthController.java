package com.taiphan74.social.modules.auth.controller;

import com.taiphan74.social.modules.auth.dto.AuthResponse;
import com.taiphan74.social.modules.auth.dto.LoginRequest;
import com.taiphan74.social.modules.auth.dto.RegisterRequest;
import com.taiphan74.social.modules.auth.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, Login, Refresh, Logout")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account and returns JWT access token. Refresh token is set in HttpOnly cookie.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registration successful",
                     content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists"),
        @ApiResponse(responseCode = "422", description = "Validation error")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                 HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(request, response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT access token. Refresh token is set in HttpOnly cookie.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful",
                     content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses HttpOnly refresh token cookie to issue a new access token. Old refresh token is rotated.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New access token generated"),
        @ApiResponse(responseCode = "401", description = "Invalid or missing refresh token")
    })
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request,
                                                HttpServletResponse response) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Clears refresh token cookie and removes all refresh tokens for the user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logged out successfully")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }
}
