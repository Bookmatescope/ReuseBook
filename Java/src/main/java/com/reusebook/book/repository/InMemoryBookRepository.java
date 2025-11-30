package com.reusebook.book.repository;

import com.reusebook.book.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 简易内存实现：使用并发 Map 模拟数据存储
 * 
 * @author 赖顺炜
 */
@Repository
public class InMemoryBookRepository implements BookRepository {

    private final Map<UUID, Book> books = new ConcurrentHashMap<>();

    @Override
    public synchronized Book save(Book book) {
        books.put(book.id(), book);
        return book;
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    @Override
    public List<Book> findByIsbn(String isbn) {
        if (isbn == null) {
            return List.of();
        }
        String normalized = isbn.replaceAll("[- ]", "").toLowerCase();
        return books.values().stream()
                .filter(book -> book.isbn().replaceAll("[- ]", "").equalsIgnoreCase(normalized))
                .toList();
    }
    
    @Override
    public List<Book> findHotBooks(int limit) {
        // 按创建时间倒序排列，模拟热点书籍
        return books.values().stream()
                .sorted(Comparator.comparing(Book::createdAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findBySellerId(UUID sellerId) {
        // 由于当前Book模型使用sellerEmail而非sellerId
        // 此方法返回空列表，实际使用需要按sellerEmail查询
        return new ArrayList<>();
    }
    
    /**
     * 按卖家邮箱查询书籍
     */
    public List<Book> findBySellerEmail(String sellerEmail) {
        return books.values().stream()
                .filter(book -> book.sellerEmail().equals(sellerEmail))
                .sorted(Comparator.comparing(Book::createdAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String lowerKeyword = keyword.toLowerCase();
        return books.values().stream()
                .filter(book -> 
                    book.title().toLowerCase().contains(lowerKeyword) ||
                    book.author().toLowerCase().contains(lowerKeyword) ||
                    book.isbn().contains(lowerKeyword) ||
                    (book.description() != null && book.description().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
}
