package com.reusebook.cart.dto;

import jakarta.validation.constraints.Min;

/**
 * 更新购物车数量请求
 */
public record UpdateCartItemRequest(
        @Min(value = 1, message = "数量至少为 1")
        int quantity
) {
}
