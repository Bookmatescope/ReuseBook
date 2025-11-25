package com.reusebook.cart.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 购物车条目模型：记录书籍与买家关系
 */
public record CartItem(
        UUID id,
        UUID bookId,
        String buyerEmail,
        String bookTitle,
        BigDecimal unitPrice,
        int quantity,
        Instant addedAt
) {
}
