package com.reusebook.book.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reusebook.book.dto.IsbnLookupResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ISBN 查询服务：调用探数 API 查询图书信息
 * API 文档：https://www.tanshuapi.com/market/detail-500
 */
@Service
public class IsbnLookupService {

    private static final Logger log = LoggerFactory.getLogger(IsbnLookupService.class);

    private static final String API_URL = "https://api.tanshuapi.com/api/isbn_base/v1/index";

    @Value("${reusebook.isbn.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 本地缓存：避免重复查询
    private final Map<String, IsbnLookupResponse> cache = new ConcurrentHashMap<>();

    public IsbnLookupService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Optional<IsbnLookupResponse> lookup(String rawIsbn) {
        if (rawIsbn == null || rawIsbn.isBlank()) {
            return Optional.empty();
        }
        String normalized = normalize(rawIsbn);

        // 检查 ISBN 格式
        if (normalized.length() < 10 || normalized.length() > 13) {
            log.warn("无效的 ISBN 格式: {}", rawIsbn);
            return Optional.empty();
        }

        // 检查 API Key 是否配置
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("ISBN API Key 未配置，返回占位信息");
            return createPlaceholder(normalized);
        }

        // 先从缓存中查找
        IsbnLookupResponse cached = cache.get(normalized);
        if (cached != null) {
            log.debug("从缓存返回 ISBN: {}", normalized);
            return Optional.of(cached);
        }

        // 调用第三方 API
        try {
            String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("key", apiKey)
                    .queryParam("isbn", normalized)
                    .toUriString();

            log.info("调用 ISBN API: {}", url.replace(apiKey, "***"));

            String responseJson = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(responseJson);

            int code = root.path("code").asInt(-1);
            if (code != 1) {
                String msg = root.path("msg").asText("查询失败");
                log.warn("ISBN API 返回错误: code={}, msg={}, isbn={}", code, msg, normalized);
                return createPlaceholder(normalized);
            }

            JsonNode data = root.path("data");
            if (data.isMissingNode() || data.isNull()) {
                log.warn("ISBN API 返回空数据: isbn={}", normalized);
                return createPlaceholder(normalized);
            }

            IsbnLookupResponse response = new IsbnLookupResponse(
                    normalized,
                    getTextOrNull(data, "title"),
                    getTextOrNull(data, "author"),
                    getTextOrNull(data, "publisher"),
                    getTextOrNull(data, "pubdate"),
                    getTextOrNull(data, "img"),
                    getTextOrNull(data, "pages"),
                    getTextOrNull(data, "price"),
                    getTextOrNull(data, "binding"),
                    getTextOrNull(data, "format"),
                    getTextOrNull(data, "summary")
            );

            // 缓存结果
            cache.put(normalized, response);
            log.info("ISBN 查询成功: {} -> {}", normalized, response.title());

            return Optional.of(response);

        } catch (Exception e) {
            log.error("ISBN API 调用失败: isbn={}, error={}", normalized, e.getMessage());
            return createPlaceholder(normalized);
        }
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        cache.clear();
        log.info("ISBN 缓存已清空");
    }

    /**
     * 创建占位响应：当 API 查询失败时使用
     */
    private Optional<IsbnLookupResponse> createPlaceholder(String isbn) {
        return Optional.of(new IsbnLookupResponse(
                isbn,
                "请手动填写书名",
                "请手动填写作者",
                "未知出版社",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ));
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull() || value.asText().isBlank()) {
            return null;
        }
        return value.asText().trim();
    }

    private String normalize(String isbn) {
        return isbn.replaceAll("[- ]", "").toUpperCase();
    }
}
