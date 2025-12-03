package com.reusebook.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图片存储配置：统一维护本地目录与访问前缀
 */
@ConfigurationProperties(prefix = "reusebook.storage")
public class ImageStorageProperties {

    /**
     * 图片实际存储目录
     */
    private Path imageDir = Paths.get("uploads");

    /**
     * 静态资源访问前缀
     */
    private String publicUrlPrefix = "/uploads/";

    public Path getImageDir() {
        return imageDir.toAbsolutePath();
    }

    public void setImageDir(String imageDir) {
        this.imageDir = Paths.get(imageDir).toAbsolutePath();
    }

    public String getPublicUrlPrefix() {
        return publicUrlPrefix;
    }

    public void setPublicUrlPrefix(String publicUrlPrefix) {
        this.publicUrlPrefix = publicUrlPrefix;
    }
}
