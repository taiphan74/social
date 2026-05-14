package com.taiphan74.social.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final int code;
    private final ErrorCode errorCode;

    protected BaseException(HttpStatus status, String message) {
        this(status, message, null);
    }

    protected BaseException(HttpStatus status, String message, ErrorCode errorCode) {
        super(message);
        this.status = status;
        this.code = status.value();
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() { return status; }
    public int getCode() { return code; }
    public ErrorCode getErrorCode() { return errorCode; }
}
