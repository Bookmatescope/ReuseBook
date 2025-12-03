package com.reusebook.auth.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * 对外暴露的用户信息，脱敏后返回给前端
 */
public record UserProfile(
        UUID id,
        String email,
        String nickname,
        Instant createdAt
) {
}
