package com.taiphan74.social.modules.auth.service;

import com.taiphan74.social.modules.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    RegisterResponse register(RegisterRequest request, HttpServletResponse response);
    AuthResponse verifyOtpAndLogin(VerifyOtpRequest request, HttpServletResponse response);
    AuthResponse login(LoginRequest request, HttpServletResponse response);
    AuthResponse refresh(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
    void sendOtp(String email);
    AuthResponse forgotPassword(ForgotPasswordRequest request);
    AuthResponse resetPasswordAndLogin(ResetPasswordRequest request, HttpServletResponse response);
}
