package com.reusebook.user.dto;

import java.util.UUID;

/**
 * 收货地址响应
 */
public record AddressResponse(
        UUID id,
        String recipientName,
        String phone,
        String province,
        String city,
        String district,
        String detailAddress,
        boolean isDefault
) {}
