package com.reusebook.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 自定义业务异常：携带 HTTP 状态码便于统一处理
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
