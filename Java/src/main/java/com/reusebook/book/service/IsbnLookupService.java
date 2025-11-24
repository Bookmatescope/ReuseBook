package com.reusebook.book.service;

import com.reusebook.book.dto.IsbnLookupResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * ISBN 查询服务：当前以内置数据模拟第三方接口
 */
@Service
public class IsbnLookupService {

    private static final Map<String, IsbnLookupResponse> MOCK_DATA = Map.of(
            "9787115428028", new IsbnLookupResponse(
                    "9787115428028",
                    "深入理解计算机系统",
                    "Randal E. Bryant",
                    "机械工业出版社",
                    2016,
                    "https://example.com/csapp.jpg"
            ),
            "9787508660751", new IsbnLookupResponse(
                    "9787508660751",
                    "解忧杂货店",
                    "东野圭吾",
                    "南海出版公司",
                    2014,
                    "https://example.com/jyou.jpg"
            )
    );

    public Optional<IsbnLookupResponse> lookup(String rawIsbn) {
        if (rawIsbn == null || rawIsbn.isBlank()) {
            return Optional.empty();
        }
        String normalized = normalize(rawIsbn);
        return Optional.ofNullable(MOCK_DATA.get(normalized));
    }

    private String normalize(String isbn) {
        return isbn.replaceAll("[- ]", "").toLowerCase();
    }
}
