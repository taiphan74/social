package com.taiphan74.social.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final int code;

    protected BaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.code = status.value();
    }

    public HttpStatus getStatus() { return status; }
    public int getCode() { return code; }
}
