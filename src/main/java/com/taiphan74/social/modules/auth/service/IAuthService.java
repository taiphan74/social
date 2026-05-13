package com.taiphan74.social.modules.auth.service;

import com.taiphan74.social.modules.auth.dto.AuthResponse;
import com.taiphan74.social.modules.auth.dto.LoginRequest;
import com.taiphan74.social.modules.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request, HttpServletResponse response);
    AuthResponse login(LoginRequest request, HttpServletResponse response);
    AuthResponse refresh(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
}
