package com.reusebook.cart.repository;

import com.reusebook.cart.entity.CartItemEntity;
import com.reusebook.cart.model.CartItem;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 购物车仓储JPA实现：使用数据库存储购物车数据
 */
@Repository
@Primary
public class DatabaseCartRepository implements CartRepository {

    private final JpaCartRepository jpaCartRepository;

    public DatabaseCartRepository(JpaCartRepository jpaCartRepository) {
        this.jpaCartRepository = jpaCartRepository;
    }

    @Override
    public CartItem save(CartItem item) {
        CartItemEntity entity = toEntity(item);
        CartItemEntity saved = jpaCartRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<CartItem> findById(UUID id) {
        return jpaCartRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<CartItem> findByBuyerAndBook(String buyerEmail, UUID bookId) {
        return jpaCartRepository.findByBuyerEmailAndBookId(buyerEmail, bookId)
                .map(this::toModel);
    }

    @Override
    public List<CartItem> findByBuyer(String buyerEmail) {
        return jpaCartRepository.findByBuyerEmail(buyerEmail).stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        jpaCartRepository.deleteById(id);
    }

    private CartItemEntity toEntity(CartItem model) {
        return new CartItemEntity(
                model.id(),
                model.bookId(),
                model.buyerEmail(),
                model.bookTitle(),
                model.unitPrice(),
                model.quantity(),
                model.addedAt()
        );
    }

    private CartItem toModel(CartItemEntity entity) {
        return new CartItem(
                entity.getId(),
                entity.getBookId(),
                entity.getBuyerEmail(),
                entity.getBookTitle(),
                entity.getUnitPrice(),
                entity.getQuantity(),
                entity.getAddedAt()
        );
    }
}
