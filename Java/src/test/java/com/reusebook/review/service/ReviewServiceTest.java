package com.reusebook.review.service;

import com.reusebook.common.exception.BusinessException;
import com.reusebook.order.model.Order;
import com.reusebook.order.model.OrderItem;
import com.reusebook.order.model.OrderStatus;
import com.reusebook.order.repository.OrderRepository;
import com.reusebook.review.dto.BookRatingResponse;
import com.reusebook.review.dto.CreateReviewRequest;
import com.reusebook.review.dto.ReviewResponse;
import com.reusebook.review.model.Review;
import com.reusebook.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReviewService 单元测试
 * 
 * 测试覆盖:
 * 1. 创建评价 - 成功和失败场景
 * 2. 获取用户评价列表
 * 3. 获取订单评价
 * 4. 获取书籍评价列表
 * 5. 获取书籍平均评分
 * 
 * @author 莫圣韬 - Day7
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReviewService reviewService;

    private UUID userId;
    private UUID orderId;
    private UUID bookId;
    private UUID addressId;
    private Order completedOrder;
    private Order pendingOrder;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        bookId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        // 创建订单项
        OrderItem orderItem = new OrderItem(bookId, "测试书籍", BigDecimal.valueOf(25.00), 1);
        List<OrderItem> items = List.of(orderItem);

        completedOrder = new Order(
                orderId,
                userId,
                items,
                addressId,
                BigDecimal.valueOf(25.00),
                OrderStatus.COMPLETED,
                Instant.now()
        );

        pendingOrder = new Order(
                UUID.randomUUID(),
                userId,
                items,
                addressId,
                BigDecimal.valueOf(25.00),
                OrderStatus.PENDING,
                Instant.now()
        );
    }

    @Nested
    @DisplayName("创建评价测试")
    class CreateReviewTests {

        @Test
        @DisplayName("成功创建评价")
        void should_create_review_successfully() {
            // Given
            CreateReviewRequest request = new CreateReviewRequest(orderId, 5, "非常满意！");
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(completedOrder));
            when(reviewRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

            // When
            ReviewResponse response = reviewService.create(userId, request);

            // Then
            assertNotNull(response);
            assertEquals(orderId, response.orderId());
            assertEquals(5, response.rating());
            assertEquals("非常满意！", response.content());
            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("订单不存在时抛出异常")
        void should_throw_when_order_not_found() {
            // Given
            CreateReviewRequest request = new CreateReviewRequest(orderId, 5, "好评");
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reviewService.create(userId, request));
            assertEquals("订单不存在", exception.getMessage());
        }

        @Test
        @DisplayName("订单未完成时抛出异常")
        void should_throw_when_order_not_completed() {
            // Given
            CreateReviewRequest request = new CreateReviewRequest(pendingOrder.id(), 5, "好评");
            when(orderRepository.findById(pendingOrder.id())).thenReturn(Optional.of(pendingOrder));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reviewService.create(userId, request));
            assertEquals("只能评价已完成的订单", exception.getMessage());
        }

        @Test
        @DisplayName("非订单买家评价时抛出异常")
        void should_throw_when_not_buyer() {
            // Given
            UUID otherUserId = UUID.randomUUID();
            CreateReviewRequest request = new CreateReviewRequest(orderId, 5, "好评");
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(completedOrder));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reviewService.create(otherUserId, request));
            assertEquals("无权评价此订单", exception.getMessage());
        }

        @Test
        @DisplayName("重复评价时抛出异常")
        void should_throw_when_already_reviewed() {
            // Given
            CreateReviewRequest request = new CreateReviewRequest(orderId, 5, "好评");
            Review existingReview = new Review(UUID.randomUUID(), orderId, userId, bookId, 5, "已评价", Instant.now());
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(completedOrder));
            when(reviewRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingReview));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reviewService.create(userId, request));
            assertEquals("该订单已评价", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("查询评价测试")
    class QueryReviewTests {

        @Test
        @DisplayName("获取用户评价列表")
        void should_get_my_reviews() {
            // Given
            Review review1 = new Review(UUID.randomUUID(), orderId, userId, bookId, 5, "好评1", Instant.now());
            Review review2 = new Review(UUID.randomUUID(), UUID.randomUUID(), userId, bookId, 4, "好评2", Instant.now());
            when(reviewRepository.findByReviewerId(userId)).thenReturn(List.of(review1, review2));

            // When
            List<ReviewResponse> reviews = reviewService.getMyReviews(userId);

            // Then
            assertEquals(2, reviews.size());
        }

        @Test
        @DisplayName("获取订单评价")
        void should_get_review_by_order_id() {
            // Given
            Review review = new Review(UUID.randomUUID(), orderId, userId, bookId, 5, "好评", Instant.now());
            when(reviewRepository.findByOrderId(orderId)).thenReturn(Optional.of(review));

            // When
            ReviewResponse response = reviewService.getReviewByOrderId(orderId);

            // Then
            assertEquals(orderId, response.orderId());
            assertEquals(5, response.rating());
        }

        @Test
        @DisplayName("订单评价不存在时抛出异常")
        void should_throw_when_review_not_found() {
            // Given
            when(reviewRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reviewService.getReviewByOrderId(orderId));
            assertEquals("评价不存在", exception.getMessage());
        }

        @Test
        @DisplayName("获取书籍评价列表")
        void should_get_reviews_by_book_id() {
            // Given
            Review review1 = new Review(UUID.randomUUID(), orderId, userId, bookId, 5, "好评1", Instant.now());
            Review review2 = new Review(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), bookId, 4, "好评2", Instant.now());
            when(reviewRepository.findByBookId(bookId)).thenReturn(List.of(review1, review2));

            // When
            List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);

            // Then
            assertEquals(2, reviews.size());
        }
    }

    @Nested
    @DisplayName("书籍评分测试")
    class BookRatingTests {

        @Test
        @DisplayName("计算书籍平均评分")
        void should_calculate_average_rating() {
            // Given
            Review review1 = new Review(UUID.randomUUID(), orderId, userId, bookId, 5, "好评", Instant.now());
            Review review2 = new Review(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), bookId, 4, "还行", Instant.now());
            Review review3 = new Review(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), bookId, 3, "一般", Instant.now());
            when(reviewRepository.findByBookId(bookId)).thenReturn(List.of(review1, review2, review3));

            // When
            BookRatingResponse response = reviewService.getBookRating(bookId);

            // Then
            assertEquals(bookId, response.bookId());
            assertEquals(4.0, response.averageRating()); // (5+4+3)/3 = 4.0
            assertEquals(3, response.totalReviews());
        }

        @Test
        @DisplayName("无评价时返回零评分")
        void should_return_zero_when_no_reviews() {
            // Given
            when(reviewRepository.findByBookId(bookId)).thenReturn(List.of());

            // When
            BookRatingResponse response = reviewService.getBookRating(bookId);

            // Then
            assertEquals(bookId, response.bookId());
            assertEquals(0.0, response.averageRating());
            assertEquals(0, response.totalReviews());
        }
    }
}
