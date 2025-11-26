package com.reusebook.order.repository;

import com.reusebook.order.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单仓储抽象
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findByUserId(UUID userId);
}
