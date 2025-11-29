package com.reusebook.order.repository;

import com.reusebook.order.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单仓储抽象
 * 
 * 性能优化建议：
 * - findByUserId 操作频繁，建议在生产环境使用数据库索引
 * - 订单量大时建议添加分页，避免一次性加载所有记录
 * - 考虑使用缓存存储最近查询的订单，减少数据库压力
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findByUserId(UUID userId);
}
