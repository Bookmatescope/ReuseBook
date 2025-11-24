package com.reusebook.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfile(
        UUID id,
        String email,
        String nickname,
        Instant createdAt
) {
}
