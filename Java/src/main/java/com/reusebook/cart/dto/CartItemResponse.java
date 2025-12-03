package com.reusebook.cart.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 购物车条目响应：前端渲染所需字段
 */
public record CartItemResponse(
        UUID id,
        UUID bookId,
        String bookTitle,
        String buyerEmail,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal,
        Instant addedAt
) {
}
