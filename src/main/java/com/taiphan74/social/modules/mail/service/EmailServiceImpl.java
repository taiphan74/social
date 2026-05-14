package com.taiphan74.social.modules.mail.service;

import com.taiphan74.social.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String to, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Your OTP verification code - Wavefy");
            message.setText("Your verification code is: " + otpCode + "\n\nThis code is valid for 5 minutes. Please do not share it with anyone.");
            mailSender.send(message);
            log.info("OTP email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending OTP email to {}: {}", to, e.getMessage());
            throw new EmailSendFailedException("Unable to send OTP email, please try again later.", e);
        }
    }
}
