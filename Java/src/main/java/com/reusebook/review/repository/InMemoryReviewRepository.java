package com.reusebook.review.repository;

import com.reusebook.review.model.Review;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 评价内存仓储实现
 * 
 * @author 戴宏翔 - Day7 添加findByBookId
 */
@Repository
public class InMemoryReviewRepository implements ReviewRepository {

    private final Map<UUID, Review> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Review review) {
        storage.put(review.id(), review);
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Review> findByOrderId(UUID orderId) {
        return storage.values().stream()
                .filter(r -> r.orderId().equals(orderId))
                .findFirst();
    }

    @Override
    public List<Review> findByReviewerId(UUID reviewerId) {
        return storage.values().stream()
                .filter(r -> r.reviewerId().equals(reviewerId))
                .toList();
    }

    @Override
    public List<Review> findByBookId(UUID bookId) {
        return storage.values().stream()
                .filter(r -> r.bookId() != null && r.bookId().equals(bookId))
                .toList();
    }

    @Override
    public List<Review> findAll() {
        return new ArrayList<>(storage.values());
    }
}
