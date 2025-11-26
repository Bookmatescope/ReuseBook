package com.reusebook.user.model;

import java.util.UUID;

/**
 * 收货地址领域模型
 */
public record Address(
        UUID id,
        UUID userId,
        String recipientName,
        String phone,
        String province,
        String city,
        String district,
        String detailAddress,
        boolean isDefault
) {
    public Address {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("recipientName cannot be blank");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("phone cannot be blank");
        }
        if (detailAddress == null || detailAddress.isBlank()) {
            throw new IllegalArgumentException("detailAddress cannot be blank");
        }
    }
}
