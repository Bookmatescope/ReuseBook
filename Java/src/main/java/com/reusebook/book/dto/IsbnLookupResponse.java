package com.reusebook.book.dto;

/**
 * ISBN 查询结果：包含从第三方接口返回的图书元数据
 */
public record IsbnLookupResponse(
        String isbn,
        String title,
        String author,
        String publisher,
        String pubdate,
        String coverUrl,
        String pages,
        String price,
        String binding,
        String format,
        String summary
) {
    /**
     * 简化构造：兼容旧版本调用
     */
    public IsbnLookupResponse(String isbn, String title, String author, String publisher, int publishedYear, String coverUrl) {
        this(isbn, title, author, publisher, String.valueOf(publishedYear), coverUrl, null, null, null, null, null);
    }
}
