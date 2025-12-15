package com.reusebook.order.repository;

import com.reusebook.order.entity.OrderEntity;
import com.reusebook.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 订单JPA仓储接口
 */
@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    List<OrderEntity> findByStatus(OrderStatus status);
    
    List<OrderEntity> findByUserIdAndStatus(UUID userId, OrderStatus status);
}
