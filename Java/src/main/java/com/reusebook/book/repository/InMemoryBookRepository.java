package com.reusebook.book.repository;

import com.reusebook.book.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易内存实现：使用并发 Map 模拟数据存储
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
}
