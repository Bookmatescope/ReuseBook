package com.reusebook.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 订单项响应
 */
public record OrderItemResponse(
        UUID bookId,
        String bookTitle,
        BigDecimal price,
        int quantity
) {}
