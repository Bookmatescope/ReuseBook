package com.reusebook.media.service;

import com.reusebook.media.config.ImageStorageProperties;
import com.reusebook.media.dto.ImageUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 图片存储服务单测：校验基础上传逻辑
 */
class ImageStorageServiceTest {

    private ImageStorageService service;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("uploads-test");
        ImageStorageProperties props = new ImageStorageProperties();
        props.setImageDir(tempDir.toString());
        props.setPublicUrlPrefix("/uploads/");
        service = new ImageStorageService(props);
    }

    @Test
    void should_store_image_successfully() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo.png",
                "image/png",
                new byte[]{1, 2, 3, 4}
        );

        ImageUploadResponse response = service.store(file, "book-cover");

        assertThat(response.url()).contains("/uploads/book-cover/");
        assertThat(Files.exists(tempDir.resolve("book-cover").resolve(response.filename()))).isTrue();
    }

    @Test
    void should_reject_non_image_file() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo.txt",
                "text/plain",
                "test".getBytes()
        );

        assertThatThrownBy(() -> service.store(file, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
