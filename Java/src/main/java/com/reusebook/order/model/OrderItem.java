package com.reusebook.order.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 订单项：单本书籍信息
 */
public record OrderItem(
        UUID bookId,
        String bookTitle,
        BigDecimal price,
        int quantity
) {
    public OrderItem {
        if (bookId == null) {
            throw new IllegalArgumentException("bookId cannot be null");
        }
        if (bookTitle == null || bookTitle.isBlank()) {
            throw new IllegalArgumentException("bookTitle cannot be blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price must be non-negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }
}
