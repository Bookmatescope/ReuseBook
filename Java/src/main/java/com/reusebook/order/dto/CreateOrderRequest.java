package com.reusebook.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * 创建订单请求
 */
public record CreateOrderRequest(
        @NotEmpty(message = "订单项不能为空")
        List<OrderItemRequest> items,

        @NotNull(message = "收货地址不能为空")
        UUID addressId
) {}
