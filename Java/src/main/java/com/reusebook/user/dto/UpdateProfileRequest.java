package com.reusebook.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新用户昵称请求
 */
public record UpdateProfileRequest(
        @NotBlank(message = "昵称不能为空")
        String nickname
) {}
