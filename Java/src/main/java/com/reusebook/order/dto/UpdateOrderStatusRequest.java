package com.reusebook.order.dto;

import com.reusebook.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 更新订单状态请求
 */
public record UpdateOrderStatusRequest(
        @NotNull(message = "订单状态不能为空")
        OrderStatus status
) {}
