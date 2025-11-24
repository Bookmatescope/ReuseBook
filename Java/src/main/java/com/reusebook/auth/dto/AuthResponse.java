package com.reusebook.auth.dto;

/**
 * 登录返回体：包含访问 Token 以及用户资料
 */
public record AuthResponse(String token, UserProfile profile) {
}
