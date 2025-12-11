package com.reusebook.order.repository;

import com.reusebook.order.entity.OrderEntity;
import com.reusebook.order.entity.OrderItemEntity;
import com.reusebook.order.model.Order;
import com.reusebook.order.model.OrderItem;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单仓储JPA实现：使用数据库存储订单数据
 */
@Repository
@Primary
public class DatabaseOrderRepository implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    public DatabaseOrderRepository(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity saved = jpaOrderRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaOrderRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<Order> findByUserId(UUID userId) {
        return jpaOrderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toModel)
                .toList();
    }

    private OrderEntity toEntity(Order model) {
        OrderEntity entity = new OrderEntity(
                model.id(),
                model.userId(),
                model.addressId(),
                model.totalAmount(),
                model.status(),
                model.createdAt()
        );
        // 添加订单项
        for (OrderItem item : model.items()) {
            OrderItemEntity itemEntity = new OrderItemEntity(
                    item.bookId(),
                    item.bookTitle(),
                    item.price(),
                    item.quantity()
            );
            entity.addItem(itemEntity);
        }
        return entity;
    }

    private Order toModel(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(item -> new OrderItem(
                        item.getBookId(),
                        item.getBookTitle(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();
        
        return new Order(
                entity.getId(),
                entity.getUserId(),
                items,
                entity.getAddressId(),
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
