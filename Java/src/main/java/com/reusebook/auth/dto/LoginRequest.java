package com.reusebook.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 登录请求体：仅校验邮箱与密码
 */
public record LoginRequest(
        @Email(message = "邮箱格式不正确")
        @NotBlank(message = "邮箱不能为空")
        String email,

        @NotBlank(message = "密码不能为空")
        @Size(min = 8, max = 64, message = "密码长度需在 8-64 之间")
        String password
) {
}
