package com.reusebook.book.controller;

import com.reusebook.book.dto.BookResponse;
import com.reusebook.book.dto.CreateBookRequest;
import com.reusebook.book.dto.IsbnLookupResponse;
import com.reusebook.book.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 书籍接口：涵盖 ISBN 查询与书籍上架
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * ISBN 查询：返回图书元数据
     */
    @GetMapping("/isbn/{isbn}/info")
    public ResponseEntity<IsbnLookupResponse> isbnInfo(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.lookupByIsbn(isbn));
    }

    /**
     * 获取全量书籍列表
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> list() {
        return ResponseEntity.ok(bookService.findAll());
    }

    /**
     * 书籍搜索：按关键字模糊匹配
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> search(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(bookService.search(keyword));
    }

    /**
     * 获取单本书籍详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    /**
     * 获取该 ISBN 对应的上架列表
     */
    @GetMapping("/isbn/{isbn}/listings")
    public ResponseEntity<List<BookResponse>> listings(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.findByIsbn(isbn));
    }

    /**
     * 创建新的书籍发布记录
     */
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody CreateBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(request));
    }
}
