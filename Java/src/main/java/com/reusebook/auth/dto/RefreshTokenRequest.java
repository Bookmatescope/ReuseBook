package com.reusebook.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 刷新 Token 请求体
 */
public record RefreshTokenRequest(
        @NotBlank(message = "token 不能为空")
        String token
) {
}
