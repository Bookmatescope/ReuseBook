package com.reusebook.cart.repository;

import com.reusebook.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 购物车JPA仓储接口
 */
@Repository
public interface JpaCartRepository extends JpaRepository<CartItemEntity, UUID> {
    
    List<CartItemEntity> findByBuyerEmail(String buyerEmail);
    
    Optional<CartItemEntity> findByBuyerEmailAndBookId(String buyerEmail, UUID bookId);
    
    void deleteByBuyerEmail(String buyerEmail);
    
    void deleteByBuyerEmailAndBookId(String buyerEmail, UUID bookId);
}
