package com.taiphan74.social.modules.auth.service;

import com.taiphan74.social.exception.BadRequestException;
import com.taiphan74.social.exception.UnauthorizedException;
import com.taiphan74.social.modules.auth.dto.AuthResponse;
import com.taiphan74.social.modules.auth.dto.ForgotPasswordRequest;
import com.taiphan74.social.modules.auth.dto.LoginRequest;
import com.taiphan74.social.modules.auth.dto.RegisterRequest;
import com.taiphan74.social.modules.auth.dto.RegisterResponse;
import com.taiphan74.social.modules.auth.dto.ResetPasswordRequest;
import com.taiphan74.social.modules.auth.dto.VerifyOtpRequest;
import com.taiphan74.social.modules.user.dto.UserCreateRequest;
import com.taiphan74.social.modules.user.dto.UserResponse;
import com.taiphan74.social.modules.user.service.CustomUserDetailsService;
import com.taiphan74.social.modules.user.service.IUserService;
import com.taiphan74.social.security.jwt.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final IOtpService otpService;

    private static final String REFRESH_COOKIE = "refreshToken";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

    @Override
    public RegisterResponse register(RegisterRequest request, HttpServletResponse response) {
        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setUsername(request.getUsername());
        createRequest.setPassword(request.getPassword());
        createRequest.setEmail(request.getEmail());
        userService.create(createRequest);
        otpService.generateAndSendOtp(request.getEmail());
        return new RegisterResponse("Registration successful. Please check your email to verify OTP.", request.getEmail());
    }

    @Override
    public AuthResponse verifyOtpAndLogin(VerifyOtpRequest request, HttpServletResponse response) {
        otpService.validateOtp(request.getEmail(), request.getOtpCode());
        UserResponse userResponse = userService.findByEmail(request.getEmail());
        userService.setVerified(userResponse.getId(), true);
        return buildAuthResponse(userResponse, response);
    }

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserResponse userResponse = userService.findByUsername(userDetails.getUsername());
        if (!userResponse.isVerified()) {
            throw new BadRequestException("Email not verified. Please verify OTP before logging in.");
        }
        return buildAuthResponse(userResponse, response);
    }

    @Override
    public void sendOtp(String email) {
        userService.findByEmail(email);
        otpService.generateAndSendOtp(email);
    }

    @Override
    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        userService.findByEmail(request.getEmail());
        otpService.generateAndSendOtp(request.getEmail());
        return new AuthResponse("OTP has been sent to your email.", null);
    }

    @Override
    public AuthResponse resetPasswordAndLogin(ResetPasswordRequest request, HttpServletResponse response) {
        otpService.validateOtp(request.getEmail(), request.getOtpCode());
        UserResponse userResponse = userService.findByEmail(request.getEmail());
        userService.updatePassword(userResponse.getId(), request.getNewPassword());
        return buildAuthResponse(userResponse, response);
    }

    @Override
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null || !jwtUtils.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or missing refresh token");
        }
        String username = jwtUtils.extractUsername(refreshToken);
        String familyId = jwtUtils.extractTokenFamily(refreshToken);
        String oldTokenId = jwtUtils.extractTokenId(refreshToken);
        UserResponse userResponse = userService.findByUsername(username);
        String newTokenId = UUID.randomUUID().toString();
        if (!refreshTokenService.validateAndRotate(userResponse.getId().toString(), familyId, oldTokenId, newTokenId)) {
            clearRefreshCookie(response);
            throw new UnauthorizedException("Token reuse detected, please login again");
        }
        String newRefreshToken = jwtUtils.generateRefreshToken(username, familyId, newTokenId);
        String newAccessToken = jwtUtils.generateToken(userDetailsService.loadUserByUsername(username));
        setRefreshCookie(response, newRefreshToken);
        return new AuthResponse(newAccessToken, userResponse);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken != null && jwtUtils.validateRefreshToken(refreshToken)) {
            String username = jwtUtils.extractUsername(refreshToken);
            UserResponse userResponse = userService.findByUsername(username);
            refreshTokenService.deleteAllForUser(userResponse.getId().toString());
        }
        clearRefreshCookie(response);
    }

    private AuthResponse buildAuthResponse(UserResponse userResponse, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userResponse.getUsername());
        String accessToken = jwtUtils.generateToken(userDetails);
        issueRefreshToken(response, userDetails.getUsername(), userResponse.getId().toString());
        return new AuthResponse(accessToken, userResponse);
    }

    private void issueRefreshToken(HttpServletResponse response, String username, String userId) {
        String familyId = UUID.randomUUID().toString();
        String refreshToken = jwtUtils.generateRefreshToken(username, familyId);
        String tokenId = jwtUtils.extractTokenId(refreshToken);
        refreshTokenService.save(userId, familyId, tokenId);
        setRefreshCookie(response, refreshToken);
    }

    private void setRefreshCookie(HttpServletResponse response, String value) {
        response.addCookie(buildCookie(value, COOKIE_MAX_AGE));
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        response.addCookie(buildCookie("", 0));
    }

    private Cookie buildCookie(String value, int maxAge) {
        Cookie cookie = new Cookie(REFRESH_COOKIE, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
