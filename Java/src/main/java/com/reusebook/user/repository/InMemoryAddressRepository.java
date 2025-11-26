package com.reusebook.user.repository;

import com.reusebook.user.model.Address;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 收货地址内存仓储实现
 */
@Repository
public class InMemoryAddressRepository implements AddressRepository {

    private final Map<UUID, Address> store = new ConcurrentHashMap<>();

    @Override
    public Address save(Address address) {
        store.put(address.id(), address);
        return address;
    }

    @Override
    public Optional<Address> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Address> findByUserId(UUID userId) {
        return store.values().stream()
                .filter(a -> a.userId().equals(userId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }
}
