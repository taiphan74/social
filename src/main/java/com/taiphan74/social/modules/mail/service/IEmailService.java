package com.taiphan74.social.modules.mail.service;

public interface IEmailService {
    void sendOtpEmail(String to, String otpCode);
}
