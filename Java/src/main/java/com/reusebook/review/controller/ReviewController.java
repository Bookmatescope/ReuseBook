package com.reusebook.review.controller;

import com.reusebook.auth.service.TokenService;
import com.reusebook.review.dto.CreateReviewRequest;
import com.reusebook.review.dto.ReviewResponse;
import com.reusebook.review.dto.BookRatingResponse;
import com.reusebook.review.service.ReviewService;
import com.reusebook.user.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 评价接口
 * 
 * 功能:
 * 1. 创建评价（需要Token认证，只能评价已完成的订单）
 * 2. 获取我的评价列表
 * 3. 获取订单的评价
 * 4. 获取书籍的评价列表
 * 5. 获取书籍的平均评分
 * 
 * @author 戴宏翔 - Day7 完善
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final TokenService tokenService;
    private final ProfileService profileService;

    public ReviewController(ReviewService reviewService, TokenService tokenService, ProfileService profileService) {
        this.reviewService = reviewService;
        this.tokenService = tokenService;
        this.profileService = profileService;
    }

    /**
     * 创建评价
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @RequestHeader HttpHeaders headers,
            @Valid @RequestBody CreateReviewRequest request) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(userId, request));
    }

    /**
     * 获取我的评价列表
     */
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> myReviews(@RequestHeader HttpHeaders headers) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.ok(reviewService.getMyReviews(userId));
    }

    /**
     * 获取订单的评价
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ReviewResponse> getByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(reviewService.getReviewByOrderId(orderId));
    }

    /**
     * 获取书籍的评价列表
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getByBookId(@PathVariable UUID bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId));
    }

    /**
     * 获取书籍的平均评分
     */
    @GetMapping("/book/{bookId}/rating")
    public ResponseEntity<BookRatingResponse> getBookRating(@PathVariable UUID bookId) {
        return ResponseEntity.ok(reviewService.getBookRating(bookId));
    }

    private UUID extractUserId(HttpHeaders headers) {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("缺少 Token");
        }
        String token = authHeader.substring("Bearer ".length());
        String email = tokenService.verify(token).subject();
        return profileService.getUserIdByEmail(email);
    }
}
