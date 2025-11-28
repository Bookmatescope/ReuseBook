package com.reusebook.review.controller;

import com.reusebook.auth.service.TokenService;
import com.reusebook.review.dto.CreateReviewRequest;
import com.reusebook.review.dto.ReviewResponse;
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
