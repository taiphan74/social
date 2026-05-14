package com.taiphan74.social.exception;

public enum ErrorCode {
    // 400 Bad Request
    OTP_EXPIRED,
    OTP_INVALID,
    OTP_COOLDOWN,
    OTP_DAILY_LIMIT,
    EMAIL_NOT_VERIFIED,
    INVALID_REFRESH_TOKEN,
    TOKEN_REUSE_DETECTED,

    // 401 Unauthorized
    BAD_CREDENTIALS,

    // 404 Not Found
    USER_NOT_FOUND,
    PROFILE_NOT_FOUND,
    EMAIL_NOT_FOUND,

    // 409 Conflict
    USERNAME_EXISTS,
    EMAIL_EXISTS,

    // 422 Validation
    VALIDATION_ERROR,

    // 429 Rate Limit
    RATE_LIMITED,

    // 500 Internal
    INTERNAL_ERROR,
    EMAIL_SEND_FAILED
}
