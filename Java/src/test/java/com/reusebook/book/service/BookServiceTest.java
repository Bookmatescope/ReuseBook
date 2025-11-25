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

    @Test
    void should_create_book_with_custom_title_and_author() {
        CreateBookRequest request = new CreateBookRequest(
                "9787111544937",
                "another@example.com",
                new BigDecimal("28.00"),
                "八成新",
                "自定义书名",
                "自定义作者",
                "这是自定义简介"
        );
        var response = bookService.create(request);
        assertThat(response.title()).isEqualTo("自定义书名");
        assertThat(response.author()).isEqualTo("自定义作者");
        assertThat(response.description()).isEqualTo("这是自定义简介");
    }

    @Test
    void should_return_empty_list_when_isbn_not_exists() {
        var books = bookService.findByIsbn("0000000000000");
        assertThat(books).isEmpty();
    }

    @Test
    void should_find_multiple_books_by_same_isbn() {
        bookService.create(new CreateBookRequest("9787115428028", "seller1@example.com", new BigDecimal("30"), "九成新", null, null, null));
        bookService.create(new CreateBookRequest("9787115428028", "seller2@example.com", new BigDecimal("25"), "全新", null, null, null));

        var books = bookService.findByIsbn("9787115428028");
        assertThat(books).hasSize(2);
    }

    @Test
    void should_find_all_books() {
        bookService.create(new CreateBookRequest("9787115428028", "a@test.com", new BigDecimal("20"), "八成新", null, null, null));
        bookService.create(new CreateBookRequest("9787111544937", "b@test.com", new BigDecimal("30"), "全新", null, null, null));

        var all = bookService.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void should_find_book_by_id() {
        var created = bookService.create(new CreateBookRequest("9787115428028", "seller@test.com", new BigDecimal("18"), "九成新", null, null, null));

        var found = bookService.findById(created.id());
        assertThat(found.isbn()).isEqualTo("9787115428028");
        assertThat(found.sellerEmail()).isEqualTo("seller@test.com");
    }

    @Test
    void should_throw_when_book_id_not_found() {
        assertThatThrownBy(() -> bookService.findById(java.util.UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("书籍不存在");
    }
}
