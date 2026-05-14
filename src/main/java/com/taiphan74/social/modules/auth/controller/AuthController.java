package com.taiphan74.social.modules.auth.controller;

import com.taiphan74.social.modules.auth.dto.AuthResponse;
import com.taiphan74.social.modules.auth.dto.ForgotPasswordRequest;
import com.taiphan74.social.modules.auth.dto.LoginRequest;
import com.taiphan74.social.modules.auth.dto.RegisterRequest;
import com.taiphan74.social.modules.auth.dto.RegisterResponse;
import com.taiphan74.social.modules.auth.dto.ResetPasswordRequest;
import com.taiphan74.social.modules.auth.dto.SendOtpRequest;
import com.taiphan74.social.modules.auth.dto.VerifyOtpRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, Login, OTP, Refresh, Logout, Forgot Password")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user, sends OTP email. Returns registration confirmation; must verify OTP to get token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registration successful, OTP sent",
                     content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists"),
        @ApiResponse(responseCode = "422", description = "Validation error")
    })
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request,
                                                     HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(request, response));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP and login", description = "Validates OTP code, activates email, and returns JWT access token. Refresh token is set in HttpOnly cookie.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email verified and logged in",
                     content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    })
    public ResponseEntity<AuthResponse> verifyOtpAndLogin(@Valid @RequestBody VerifyOtpRequest request,
                                                          HttpServletResponse response) {
        return ResponseEntity.ok(authService.verifyOtpAndLogin(request, response));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Resends OTP code to email (cooldown 60s, max 5 per day).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP sent"),
        @ApiResponse(responseCode = "400", description = "Too many requests or cooldown active")
    })
    public ResponseEntity<Void> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.sendOtp(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user (email must be verified) and return JWT access token. Refresh token is set in HttpOnly cookie.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful",
                     content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Email not verified"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses HttpOnly refresh token cookie to issue a new access token. Old refresh token is rotated.")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request,
                                                HttpServletResponse response) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Clears refresh token cookie and removes all refresh tokens for the user.")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Sends OTP to email for password reset.")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password and login", description = "Verifies OTP, resets password, and returns access token. Refresh token set in cookie.")
    public ResponseEntity<AuthResponse> resetPasswordAndLogin(@Valid @RequestBody ResetPasswordRequest request,
                                                              HttpServletResponse response) {
        return ResponseEntity.ok(authService.resetPasswordAndLogin(request, response));
    }
}
