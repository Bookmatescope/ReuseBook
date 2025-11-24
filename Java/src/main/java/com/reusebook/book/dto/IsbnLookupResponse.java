package com.reusebook.book.dto;

/**
 * ISBN 查询结果：模拟第三方接口返回的图书元数据
 */
public record IsbnLookupResponse(
        String isbn,
        String title,
        String author,
        String publisher,
        int publishedYear,
        String coverUrl
) {
}
