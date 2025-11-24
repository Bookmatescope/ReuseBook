package com.reusebook.media.service;

import com.reusebook.media.config.ImageStorageProperties;
import com.reusebook.media.dto.ImageUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

/**
 * 图片存储服务：负责校验、命名与返回访问地址
 */
@Service
public class ImageStorageService {

    private static final long MAX_SIZE = 5 * 1024 * 1024;

    private final ImageStorageProperties properties;

    public ImageStorageService(ImageStorageProperties properties) {
        this.properties = properties;
    }

    public ImageUploadResponse store(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请上传有效图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过 5MB");
        }
        if (!isImage(file.getContentType())) {
            throw new IllegalArgumentException("仅支持上传图片类型文件");
        }
        String sanitizedCategory = sanitizeCategory(category);
        String extension = resolveExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;
        Path targetDir = sanitizedCategory == null
                ? properties.getImageDir()
                : properties.getImageDir().resolve(sanitizedCategory);
        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            String url = buildPublicUrl(sanitizedCategory, filename);
            return new ImageUploadResponse(filename, url, file.getSize(), sanitizedCategory, Instant.now());
        } catch (IOException ex) {
            throw new IllegalStateException("图片上传失败，请稍后重试", ex);
        }
    }

    private boolean isImage(String contentType) {
        return contentType != null && contentType.toLowerCase(Locale.ROOT).startsWith("image/");
    }

    private String resolveExtension(String originalName) {
        if (originalName == null) {
            return ".png";
        }
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
        if (ext.isBlank()) {
            return ".png";
        }
        return ext.toLowerCase(Locale.ROOT);
    }

    private String sanitizeCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }
        return category.trim().replaceAll("[^a-zA-Z0-9-_]", "-");
    }

    private String buildPublicUrl(String category, String filename) {
        String prefix = properties.getPublicUrlPrefix();
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        if (category != null && !category.isBlank()) {
            return prefix + category + "/" + filename;
        }
        return prefix + filename;
    }
}
