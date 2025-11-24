package com.reusebook.auth.service;

import java.time.Instant;

public record TokenPayload(String subject, Instant issuedAt) {
}
