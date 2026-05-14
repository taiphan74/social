package com.taiphan74.social.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(HttpStatus.UNAUTHORIZED, message, errorCode);
    }
}
