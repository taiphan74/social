package com.taiphan74.social.exception;

import org.springframework.http.HttpStatus;
import java.util.List;

public class ValidationException extends BaseException {
    private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(HttpStatus.valueOf(422), message);
        this.errors = errors;
    }

    public List<String> getErrors() { return errors; }
}
