package com.reusebook.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * 创建评价请求
 */
public record CreateReviewRequest(
        @NotNull(message = "订单ID不能为空")
        UUID orderId,

        @Min(value = 1, message = "评分最低1星")
        @Max(value = 5, message = "评分最高5星")
        int rating,

        String content
) {}
