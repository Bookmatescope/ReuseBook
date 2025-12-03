package com.reusebook.user.service;

import com.reusebook.user.dto.AddressRequest;
import com.reusebook.user.dto.AddressResponse;
import com.reusebook.user.model.Address;
import com.reusebook.user.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 收货地址服务
 */
@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * 获取用户所有收货地址
     */
    public List<AddressResponse> getAddresses(UUID userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 添加收货地址
     */
    public AddressResponse addAddress(UUID userId, AddressRequest request) {
        // 如果新地址是默认，需要先清除其他默认
        if (request.isDefault()) {
            clearDefaultAddresses(userId);
        }

        Address address = new Address(
                UUID.randomUUID(),
                userId,
                request.recipientName(),
                request.phone(),
                request.province(),
                request.city(),
                request.district(),
                request.detailAddress(),
                request.isDefault()
        );
        return toResponse(addressRepository.save(address));
    }

    /**
     * 更新收货地址
     */
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request) {
        Address existing = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        if (!existing.userId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此地址");
        }

        // 如果设为默认，清除其他默认
        if (request.isDefault()) {
            clearDefaultAddresses(userId);
        }

        Address updated = new Address(
                addressId,
                userId,
                request.recipientName(),
                request.phone(),
                request.province(),
                request.city(),
                request.district(),
                request.detailAddress(),
                request.isDefault()
        );
        return toResponse(addressRepository.save(updated));
    }

    /**
     * 删除收货地址
     */
    public void deleteAddress(UUID userId, UUID addressId) {
        Address existing = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        if (!existing.userId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此地址");
        }

        addressRepository.deleteById(addressId);
    }

    private void clearDefaultAddresses(UUID userId) {
        addressRepository.findByUserId(userId).stream()
                .filter(Address::isDefault)
                .forEach(addr -> {
                    Address cleared = new Address(
                            addr.id(),
                            addr.userId(),
                            addr.recipientName(),
                            addr.phone(),
                            addr.province(),
                            addr.city(),
                            addr.district(),
                            addr.detailAddress(),
                            false
                    );
                    addressRepository.save(cleared);
                });
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.id(),
                address.recipientName(),
                address.phone(),
                address.province(),
                address.city(),
                address.district(),
                address.detailAddress(),
                address.isDefault()
        );
    }
}
