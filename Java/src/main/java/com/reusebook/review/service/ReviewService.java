package com.reusebook.review.service;

import com.reusebook.common.exception.BusinessException;
import com.reusebook.order.model.Order;
import com.reusebook.order.model.OrderStatus;
import com.reusebook.order.repository.OrderRepository;
import com.reusebook.review.dto.BookRatingResponse;
import com.reusebook.review.dto.CreateReviewRequest;
import com.reusebook.review.dto.ReviewResponse;
import com.reusebook.review.model.Review;
import com.reusebook.review.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 评价服务
 * 
 * 功能:
 * 1. 创建评价（仅已完成订单可评价）
 * 2. 获取用户的评价列表
 * 3. 获取订单的评价
 * 4. 获取书籍的评价列表
 * 5. 获取书籍的平均评分
 * 
 * @author 戴宏翔 - Day7 完善
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 创建评价（仅已完成订单可评价）
     */
    public ReviewResponse create(UUID reviewerId, CreateReviewRequest request) {
        // 检查订单是否存在且已完成
        var order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在"));

        if (order.status() != OrderStatus.COMPLETED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "只能评价已完成的订单");
        }

        if (!order.userId().equals(reviewerId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "无权评价此订单");
        }

        // 检查是否已评价
        if (reviewRepository.findByOrderId(request.orderId()).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "该订单已评价");
        }

        // 从订单项中获取第一本书的ID（简化处理，假设一个订单对应一本书）
        UUID bookId = order.items().isEmpty() ? null : order.items().get(0).bookId();

        Review review = new Review(
                UUID.randomUUID(),
                request.orderId(),
                reviewerId,
                bookId,
                request.rating(),
                request.content() != null ? request.content() : "",
                Instant.now()
        );

        reviewRepository.save(review);
        return toResponse(review);
    }

    /**
     * 获取用户的评价列表
     */
    public List<ReviewResponse> getMyReviews(UUID reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 获取订单的评价
     */
    public ReviewResponse getReviewByOrderId(UUID orderId) {
        return reviewRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "评价不存在"));
    }

    /**
     * 获取书籍的评价列表
     */
    public List<ReviewResponse> getReviewsByBookId(UUID bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 获取书籍的平均评分
     */
    public BookRatingResponse getBookRating(UUID bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        if (reviews.isEmpty()) {
            return new BookRatingResponse(bookId, 0.0, 0);
        }
        double avgRating = reviews.stream()
                .mapToInt(Review::rating)
                .average()
                .orElse(0.0);
        return new BookRatingResponse(bookId, Math.round(avgRating * 10) / 10.0, reviews.size());
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.id(),
                review.orderId(),
                review.reviewerId(),
                review.rating(),
                review.content(),
                review.createdAt()
        );
    }
}
