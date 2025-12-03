package com.reusebook.cart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * 购物车新增请求：提供买家邮箱与目标书籍信息
 */
public record AddCartItemRequest(
        @NotNull(message = "书籍 ID 不能为空")
        UUID bookId,

        @NotBlank(message = "买家邮箱不能为空")
        @Email(message = "买家邮箱格式不正确")
        String buyerEmail,

        @Min(value = 1, message = "数量至少为 1")
        int quantity
) {
}
