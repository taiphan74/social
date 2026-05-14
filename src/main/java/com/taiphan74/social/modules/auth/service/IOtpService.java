package com.taiphan74.social.modules.auth.service;

public interface IOtpService {
    void generateAndSendOtp(String email);
    void validateOtp(String email, String otpCode);
}
