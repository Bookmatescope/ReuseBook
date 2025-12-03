package com.reusebook.media.dto;

import java.time.Instant;

/**
 * 图片上传响应
 */
public record ImageUploadResponse(
        String filename,
        String url,
        long size,
        String category,
        Instant uploadedAt
) {
}
