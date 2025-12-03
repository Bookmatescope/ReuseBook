package com.reusebook.review.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * 评价响应
 */
public record ReviewResponse(
        UUID id,
        UUID orderId,
        UUID reviewerId,
        int rating,
        String content,
        Instant createdAt
) {}
