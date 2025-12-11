package com.reusebook.review.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * 评价实体：映射数据库reviews表
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_reviews_order", columnList = "order_id"),
    @Index(name = "idx_reviews_book", columnList = "book_id"),
    @Index(name = "idx_reviews_reviewer", columnList = "reviewer_id")
})
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;

    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ReviewEntity() {
    }

    public ReviewEntity(UUID id, UUID orderId, UUID reviewerId, UUID bookId, 
                        int rating, String content, Instant createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.reviewerId = reviewerId;
        this.bookId = bookId;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(UUID reviewerId) {
        this.reviewerId = reviewerId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
