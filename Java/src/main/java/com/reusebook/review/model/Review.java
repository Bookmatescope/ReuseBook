package com.reusebook.review.model;

import java.time.Instant;
import java.util.UUID;

/**
 * 评价领域模型
 */
public record Review(
        UUID id,
        UUID orderId,
        UUID reviewerId,
        int rating,           // 1-5 星评分
        String content,       // 评价内容
        Instant createdAt
) {
    public Review {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        if (reviewerId == null) {
            throw new IllegalArgumentException("reviewerId cannot be null");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt cannot be null");
        }
    }
}
