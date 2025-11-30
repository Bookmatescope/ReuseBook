package com.reusebook.review.repository;

import com.reusebook.review.model.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 评价仓储接口
 * 
 * @author 戴宏翔 - Day7 添加findByBookId
 */
public interface ReviewRepository {
    void save(Review review);
    Optional<Review> findById(UUID id);
    Optional<Review> findByOrderId(UUID orderId);
    List<Review> findByReviewerId(UUID reviewerId);
    List<Review> findByBookId(UUID bookId);
    List<Review> findAll();
}
