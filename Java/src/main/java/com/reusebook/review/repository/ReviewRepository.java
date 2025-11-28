package com.reusebook.review.repository;

import com.reusebook.review.model.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 评价仓储接口
 */
public interface ReviewRepository {
    void save(Review review);
    Optional<Review> findById(UUID id);
    Optional<Review> findByOrderId(UUID orderId);
    List<Review> findByReviewerId(UUID reviewerId);
    List<Review> findAll();
}
