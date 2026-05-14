package com.taiphan74.social.modules.auth.service;

import com.taiphan74.social.exception.BadRequestException;
import com.taiphan74.social.exception.ErrorCode;
import com.taiphan74.social.modules.mail.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements IOtpService {

    private final StringRedisTemplate redisTemplate;
    private final IEmailService emailService;

    private static final long OTP_TTL = 5;
    private static final long COOLDOWN_TTL = 60;
    private static final int MAX_ATTEMPTS = 5;

    @Override
    public void generateAndSendOtp(String email) {
        String cooldownKey = "otp:cooldown:" + email;
        String attemptsKey = "otp:attempts:" + email + ":" + LocalDate.now();
        String otpKey = "otp:code:" + email;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new BadRequestException("Please wait 60 seconds before requesting a new code.", ErrorCode.OTP_COOLDOWN);
        }

        String attemptsStr = redisTemplate.opsForValue().get(attemptsKey);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
        if (attempts >= MAX_ATTEMPTS) {
            throw new BadRequestException("You have exceeded 5 OTP requests today. Please try again tomorrow.", ErrorCode.OTP_DAILY_LIMIT);
        }

        String otpCode = String.format("%06d", new Random().nextInt(999999));

        redisTemplate.opsForValue().set(otpKey, otpCode, OTP_TTL, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(cooldownKey, "1", COOLDOWN_TTL, TimeUnit.SECONDS);

        if (attempts == 0) {
            redisTemplate.opsForValue().set(attemptsKey, "1", 24, TimeUnit.HOURS);
        } else {
            redisTemplate.opsForValue().increment(attemptsKey);
        }

        emailService.sendOtpEmail(email, otpCode);
        log.info("Generated and sent OTP to email: {}", email);
    }

    @Override
    public void validateOtp(String email, String otpCode) {
        String otpKey = "otp:code:" + email;
        String savedOtp = redisTemplate.opsForValue().get(otpKey);

        if (savedOtp == null) {
            throw new BadRequestException("OTP code has expired or does not exist.", ErrorCode.OTP_EXPIRED);
        }

        if (!savedOtp.equals(otpCode)) {
            throw new BadRequestException("OTP code is incorrect.", ErrorCode.OTP_INVALID);
        }

        redisTemplate.delete(otpKey);
        log.info("OTP verified successfully for email: {}", email);
    }
}
