package com.taiphan74.social.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String message, ErrorCode errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }
}
