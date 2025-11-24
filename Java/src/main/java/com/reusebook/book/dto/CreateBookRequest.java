package com.reusebook.book.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 创建书籍请求：包含 ISBN、售价、成色与卖家信息
 */
public record CreateBookRequest(
        @NotBlank(message = "ISBN 不能为空")
        @Size(min = 10, max = 13, message = "ISBN 长度需为 10-13 位")
        String isbn,

        @NotBlank(message = "卖家邮箱不能为空")
        @Email(message = "卖家邮箱格式不正确")
        String sellerEmail,

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.1", message = "价格至少为 0.1")
        BigDecimal price,

        @NotBlank(message = "图书成色不能为空")
        String condition,

        String title,
        String author,
        String description
) {
}
