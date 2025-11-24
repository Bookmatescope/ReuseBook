package com.reusebook.auth.service;

import java.time.Instant;

/**
 * Token 解析结果：记录 subject 及签发时间
 */
public record TokenPayload(String subject, Instant issuedAt) {
}
