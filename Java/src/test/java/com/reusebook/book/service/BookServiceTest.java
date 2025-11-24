package com.reusebook.book.service;

import com.reusebook.book.dto.CreateBookRequest;
import com.reusebook.book.repository.InMemoryBookRepository;
import com.reusebook.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 书籍服务单元测试：覆盖 ISBN 查询与发布创建
 */
class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(new InMemoryBookRepository(), new IsbnLookupService());
    }

    @Test
    void should_lookup_isbn_meta() {
        var meta = bookService.lookupByIsbn("9787115428028");
        assertThat(meta.title()).isEqualTo("深入理解计算机系统");
    }

    @Test
    void should_create_book_with_meta_defaults() {
        CreateBookRequest request = new CreateBookRequest(
                "9787115428028",
                "seller@example.com",
                new BigDecimal("35.6"),
                "九成新",
                null,
                null,
                null
        );
        var response = bookService.create(request);
        assertThat(response.title()).isEqualTo("深入理解计算机系统");
        assertThat(response.sellerEmail()).isEqualTo("seller@example.com");
        assertThat(bookService.findByIsbn("9787115428028")).hasSize(1);
    }

    @Test
    void should_throw_when_isbn_not_found() {
        assertThatThrownBy(() -> bookService.lookupByIsbn("000"))
                .isInstanceOf(BusinessException.class);
    }
}
