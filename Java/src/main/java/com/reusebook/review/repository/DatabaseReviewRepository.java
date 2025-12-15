package com.reusebook.review.repository;

import com.reusebook.review.entity.ReviewEntity;
import com.reusebook.review.model.Review;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 评价仓储JPA实现：使用数据库存储评价数据
 */
@Repository
@Primary
public class DatabaseReviewRepository implements ReviewRepository {

    private final JpaReviewRepository jpaReviewRepository;

    public DatabaseReviewRepository(JpaReviewRepository jpaReviewRepository) {
        this.jpaReviewRepository = jpaReviewRepository;
    }

    @Override
    public void save(Review review) {
        ReviewEntity entity = toEntity(review);
        jpaReviewRepository.save(entity);
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return jpaReviewRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Review> findByOrderId(UUID orderId) {
        return jpaReviewRepository.findByOrderId(orderId).map(this::toModel);
    }

    @Override
    public List<Review> findByReviewerId(UUID reviewerId) {
        return jpaReviewRepository.findByReviewerIdOrderByCreatedAtDesc(reviewerId).stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public List<Review> findByBookId(UUID bookId) {
        return jpaReviewRepository.findByBookIdOrderByCreatedAtDesc(bookId).stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public List<Review> findAll() {
        return jpaReviewRepository.findAll().stream()
                .map(this::toModel)
                .toList();
    }

    private ReviewEntity toEntity(Review model) {
        return new ReviewEntity(
                model.id(),
                model.orderId(),
                model.reviewerId(),
                model.bookId(),
                model.rating(),
                model.content(),
                model.createdAt()
        );
    }

    private Review toModel(ReviewEntity entity) {
        return new Review(
                entity.getId(),
                entity.getOrderId(),
                entity.getReviewerId(),
                entity.getBookId(),
                entity.getRating(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
