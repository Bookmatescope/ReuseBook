package com.reusebook.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * 订单项请求
 */
public record OrderItemRequest(
        @NotNull(message = "书籍ID不能为空")
        UUID bookId,

        @Min(value = 1, message = "数量至少为1")
        int quantity
) {}
