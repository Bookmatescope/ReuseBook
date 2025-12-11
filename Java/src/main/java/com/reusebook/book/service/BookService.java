package com.reusebook.book.service;

import com.reusebook.book.dto.BookResponse;
import com.reusebook.book.dto.CreateBookRequest;
import com.reusebook.book.dto.IsbnLookupResponse;
import com.reusebook.book.model.Book;
import com.reusebook.book.repository.BookRepository;
import com.reusebook.common.exception.BusinessException;
import com.reusebook.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.math.RoundingMode;

/**
 * 书籍服务：封装 ISBN 查询与上架创建逻辑
 */
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final IsbnLookupService isbnLookupService;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, IsbnLookupService isbnLookupService, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.isbnLookupService = isbnLookupService;
        this.userRepository = userRepository;
    }

    /**
     * 根据 ISBN 查询基础信息
     */
    public IsbnLookupResponse lookupByIsbn(String isbn) {
        return isbnLookupService.lookup(isbn)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "未查询到对应 ISBN"));
    }

    /**
     * 创建书籍记录：若缺少标题/作者则回填 ISBN 数据
     */
    public BookResponse create(CreateBookRequest request) {
        IsbnLookupResponse meta = isbnLookupService.lookup(request.isbn()).orElse(null);
        String title = pickValue(request.title(), meta != null ? meta.title() : null, "待补充书名");
        String author = pickValue(request.author(), meta != null ? meta.author() : null, "佚名作者");
        String description = pickValue(request.description(), meta != null ? meta.publisher() : null, "尚未填写简介");
        // 封面图：优先使用用户提供的，其次使用 ISBN 查询的
        String coverUrl = pickValue(request.coverUrl(), meta != null ? meta.coverUrl() : null, null);
        Book book = new Book(
                UUID.randomUUID(),
                normalizeIsbn(request.isbn()),
                title,
                author,
                description,
                request.price(),
                request.condition(),
                request.sellerEmail(),
                request.meetupLocation(),
                coverUrl,
                Instant.now()
        );
        bookRepository.save(book);
        return toResponse(book);
    }

    /**
     * 根据 ISBN 查看当前已发布的书籍列表
     */
    public List<BookResponse> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(normalizeIsbn(isbn)).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 获取全量书籍列表
     */
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 根据 ID 获取书籍详情
     */
    public BookResponse findById(UUID id) {
        return bookRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "书籍不存在"));
    }

    /**
     * 书籍搜索：支持按关键字模糊匹配标题、作者、ISBN
     */
    public List<BookResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String lowerKeyword = keyword.toLowerCase();
        return bookRepository.findAll().stream()
                .filter(book -> 
                    book.title().toLowerCase().contains(lowerKeyword) ||
                    book.author().toLowerCase().contains(lowerKeyword) ||
                    book.isbn().contains(keyword.replaceAll("[- ]", "")))
                .map(this::toResponse)
                .toList();
    }

    private String pickValue(String preferred, String fallback, String defaultValue) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return defaultValue;
    }

    private String normalizeIsbn(String isbn) {
        if (isbn == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ISBN 不能为空");
        }
        return isbn.replaceAll("[- ]", "");
    }

    private BookResponse toResponse(Book book) {
        // 获取卖家昵称
        String sellerNickname = userRepository.findByEmail(book.sellerEmail())
                .map(user -> user.nickname())
                .orElse(null);
        return new BookResponse(
                book.id(),
                book.isbn(),
                book.title(),
                book.author(),
                book.description(),
                book.price().setScale(2, RoundingMode.HALF_UP),
                book.condition(),
                book.sellerEmail(),
                sellerNickname,
                book.meetupLocation(),
                book.coverUrl(),
                book.createdAt()
        );
    }
}
