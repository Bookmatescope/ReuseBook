package com.reusebook.review.service;

import com.reusebook.common.exception.BusinessException;
import com.reusebook.order.model.OrderStatus;
import com.reusebook.order.repository.OrderRepository;
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

        Review review = new Review(
                UUID.randomUUID(),
                request.orderId(),
                reviewerId,
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
