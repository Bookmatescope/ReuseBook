package com.reusebook.media.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 存储相关配置：负责创建目录并暴露静态资源映射
 */
@Configuration
@EnableConfigurationProperties(ImageStorageProperties.class)
public class StorageConfig implements WebMvcConfigurer {

    private final ImageStorageProperties properties;

    public StorageConfig(ImageStorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void ensureDirectoryExists() {
        try {
            Path dir = properties.getImageDir();
            Files.createDirectories(dir);
        } catch (Exception ex) {
            throw new IllegalStateException("无法创建图片存储目录", ex);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pattern = properties.getPublicUrlPrefix();
        if (!pattern.endsWith("/**")) {
            pattern = StringUtils.trimTrailingCharacter(pattern, '/') + "/**";
        }
        String location = properties.getImageDir().toUri().toString();
        registry.addResourceHandler(pattern)
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
