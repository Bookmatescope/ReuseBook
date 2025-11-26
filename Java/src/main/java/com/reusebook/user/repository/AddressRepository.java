package com.reusebook.user.repository;

import com.reusebook.user.model.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 收货地址仓储抽象
 */
public interface AddressRepository {

    Address save(Address address);

    Optional<Address> findById(UUID id);

    List<Address> findByUserId(UUID userId);

    void deleteById(UUID id);
}
