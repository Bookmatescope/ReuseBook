package com.reusebook.book.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 书籍领域模型：包含交易所需的基础信息
 */
public record Book(
        UUID id,
        String isbn,
        String title,
        String author,
        String description,
        BigDecimal price,
        String condition,
        String sellerEmail,
        String meetupLocation,  // 面交地址（卖家提供）
        Instant createdAt
) {
}
