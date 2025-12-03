package com.reusebook.review.dto;

import java.util.UUID;

/**
 * 书籍评分响应DTO
 * 
 * @author 戴宏翔 - Day7
 */
public record BookRatingResponse(
    UUID bookId,
    Double averageRating,
    Integer totalReviews
) {}
