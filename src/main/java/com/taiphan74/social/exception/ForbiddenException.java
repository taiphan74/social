package com.taiphan74.social.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
