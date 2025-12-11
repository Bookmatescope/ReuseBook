package com.reusebook.review.repository;

import com.reusebook.review.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 评价JPA仓储接口
 */
@Repository
public interface JpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    
    Optional<ReviewEntity> findByOrderId(UUID orderId);
    
    List<ReviewEntity> findByBookIdOrderByCreatedAtDesc(UUID bookId);
    
    List<ReviewEntity> findByReviewerIdOrderByCreatedAtDesc(UUID reviewerId);
    
    boolean existsByOrderId(UUID orderId);
    
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.bookId = :bookId")
    Double getAverageRatingByBookId(UUID bookId);
}
