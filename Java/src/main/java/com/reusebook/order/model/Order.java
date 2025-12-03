package com.reusebook.order.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 订单领域模型
 */
public record Order(
        UUID id,
        UUID userId,
        List<OrderItem> items,
        UUID addressId,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt
) {
    public Order {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items cannot be empty");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("totalAmount must be non-negative");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt cannot be null");
        }
    }
}
