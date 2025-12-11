package com.reusebook.order.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/**
 * 创建订单请求
 * 面交模式下 addressId 为可选
 */
public record CreateOrderRequest(
        @NotEmpty(message = "订单项不能为空")
        List<OrderItemRequest> items,

        UUID addressId
) {}
