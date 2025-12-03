package com.reusebook.cart.repository;

import com.reusebook.cart.model.CartItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存版购物车仓储：按买家邮箱管理条目
 */
@Repository
public class InMemoryCartRepository implements CartRepository {

    private final Map<UUID, CartItem> storage = new ConcurrentHashMap<>();

    @Override
    public synchronized CartItem save(CartItem item) {
        storage.put(item.id(), item);
        return item;
    }

    @Override
    public Optional<CartItem> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<CartItem> findByBuyerAndBook(String buyerEmail, UUID bookId) {
        if (buyerEmail == null || bookId == null) {
            return Optional.empty();
        }
        String normalizedEmail = buyerEmail.toLowerCase();
        return storage.values().stream()
                .filter(item -> item.buyerEmail().equalsIgnoreCase(normalizedEmail) && item.bookId().equals(bookId))
                .findFirst();
    }

    @Override
    public List<CartItem> findByBuyer(String buyerEmail) {
        if (buyerEmail == null) {
            return List.of();
        }
        String normalizedEmail = buyerEmail.toLowerCase();
        return storage.values().stream()
                .filter(item -> item.buyerEmail().equalsIgnoreCase(normalizedEmail))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        storage.remove(id);
    }
}
