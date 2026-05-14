package com.taiphan74.social.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taiphan74.social.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API Response wrapper")
public class ApiResponse<T> {
    @Schema(description = "HTTP status code", example = "200")
    private int code;
    @Schema(description = "Error code (null if success)", example = "OTP_EXPIRED")
    private ErrorCode errorCode;
    @Schema(description = "Response message", example = "OK")
    private String message;
    @Schema(description = "Response data")
    private T data;
    @Schema(description = "Validation errors")
    private List<String> errors;
    @Schema(description = "Response timestamp")
    private LocalDateTime timestamp;

    public ApiResponse(int code, ErrorCode errorCode, String message, T data) {
        this.code = code;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(int code, ErrorCode errorCode, String message, List<String> errors) {
        this.code = code;
        this.errorCode = errorCode;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, null, "OK", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, null, message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, null, "Created", data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, null, message, data);
    }

    public static <T> ApiResponse<T> error(int code, ErrorCode errorCode, String message) {
        return new ApiResponse<>(code, errorCode, message, (T) null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, null, message, (T) null);
    }

    public static ApiResponse<Void> validationError(String message, List<String> errors) {
        return new ApiResponse<>(422, ErrorCode.VALIDATION_ERROR, message, errors);
    }

    public int getCode() { return code; }
    public ErrorCode getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public List<String> getErrors() { return errors; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
