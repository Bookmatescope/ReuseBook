package com.reusebook.cart.repository;

import com.reusebook.cart.model.CartItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 购物车仓储抽象
 */
public interface CartRepository {

    CartItem save(CartItem item);

    Optional<CartItem> findById(UUID id);

    Optional<CartItem> findByBuyerAndBook(String buyerEmail, UUID bookId);

    List<CartItem> findByBuyer(String buyerEmail);

    void delete(UUID id);
}
