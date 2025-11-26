package com.reusebook.order.dto;

import com.reusebook.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 订单响应
 */
public record OrderResponse(
        UUID id,
        List<OrderItemResponse> items,
        UUID addressId,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt
) {}
