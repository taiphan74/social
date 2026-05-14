package com.taiphan74.social.modules.mail.service;

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
            message.setSubject("Mã xác thực OTP của bạn - Wavefy");
            message.setText("Mã xác thực của bạn là: " + otpCode + "\n\nMã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ cho người khác.");
            
            mailSender.send(message);
            log.info("Email OTP đã được gửi tới: {}", to);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email OTP tới {}: {}", to, e.getMessage());
            throw new RuntimeException("Không thể gửi email OTP, vui lòng thử lại sau.");
        }
    }
}
