package com.reusebook.common.exception;

import java.time.Instant;

/**
 * 错误响应体：统一记录时间、状态码、错误与描述
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(Instant.now(), status, error, message);
    }
}
