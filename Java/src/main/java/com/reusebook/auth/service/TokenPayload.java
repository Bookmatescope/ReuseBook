package com.reusebook.auth.service;

import java.time.Instant;

/**
 * Token 解析结果：记录 subject、签发与过期时间
 */
public record TokenPayload(String subject, Instant issuedAt, Instant expiresAt) {
}
