package com.taiphan74.social.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, ErrorCode.USER_NOT_FOUND);
    }

    public NotFoundException(String message, ErrorCode errorCode) {
        super(HttpStatus.NOT_FOUND, message, errorCode);
    }
}
