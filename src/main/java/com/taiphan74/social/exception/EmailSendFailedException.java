package com.taiphan74.social.exception;

public class EmailSendFailedException extends RuntimeException {
    public EmailSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
