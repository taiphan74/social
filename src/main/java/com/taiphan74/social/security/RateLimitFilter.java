package com.taiphan74.social.security;

import com.taiphan74.social.config.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Value("${app.rate-limit.register:5}")
    private int registerLimit;

    @Value("${app.rate-limit.login:10}")
    private int loginLimit;

    @Value("${app.rate-limit.send-otp:3}")
    private int sendOtpLimit;

    @Value("${app.rate-limit.verify-otp:5}")
    private int verifyOtpLimit;

    @Value("${app.rate-limit.forgot-password:3}")
    private int forgotPasswordLimit;

    @Value("${app.rate-limit.reset-password:5}")
    private int resetPasswordLimit;

    @Value("${app.rate-limit.window-seconds:60}")
    private int windowSeconds;

    private static final Map<String, String> PATH_MAP = Map.of(
        "/api/auth/register", "register",
        "/api/auth/login", "login",
        "/api/auth/send-otp", "send-otp",
        "/api/auth/verify-otp", "verify-otp",
        "/api/auth/forgot-password", "forgot-password",
        "/api/auth/reset-password", "reset-password"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String endpoint = PATH_MAP.get(path);

        if (endpoint != null) {
            int limit = switch (endpoint) {
                case "register" -> registerLimit;
                case "login" -> loginLimit;
                case "send-otp" -> sendOtpLimit;
                case "verify-otp" -> verifyOtpLimit;
                case "forgot-password" -> forgotPasswordLimit;
                case "reset-password" -> resetPasswordLimit;
                default -> 10;
            };

            String clientIp = getClientIp(request);
            String key = endpoint + ":" + clientIp;

            if (!rateLimitService.isAllowed(key, limit, windowSeconds)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
