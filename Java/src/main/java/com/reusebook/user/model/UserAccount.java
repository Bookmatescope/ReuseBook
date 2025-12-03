package com.reusebook.user.model;

import java.time.Instant;
import java.util.UUID;

/**
 * 用户领域模型：用于存储账号核心信息
 */
public record UserAccount(
        UUID id,
        String email,
        String passwordHash,
        String nickname,
        Instant createdAt
) {
    public UserAccount {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash cannot be blank");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("nickname cannot be blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt cannot be null");
        }
    }
}
