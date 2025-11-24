package com.reusebook.book.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 对外返回的书籍信息
 */
public record BookResponse(
        UUID id,
        String isbn,
        String title,
        String author,
        String description,
        BigDecimal price,
        String condition,
        String sellerEmail,
        Instant createdAt
) {
}
