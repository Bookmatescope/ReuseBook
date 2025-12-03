package com.reusebook.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建/更新收货地址请求
 */
public record AddressRequest(
        @NotBlank(message = "收件人姓名不能为空")
        String recipientName,

        @NotBlank(message = "手机号不能为空")
        String phone,

        String province,

        String city,

        String district,

        @NotBlank(message = "详细地址不能为空")
        String detailAddress,

        boolean isDefault
) {}
