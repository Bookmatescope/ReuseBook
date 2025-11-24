package com.reusebook.book.repository;

import com.reusebook.book.model.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 书籍仓储抽象：便于后续替换为数据库实现
 */
public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(UUID id);

    List<Book> findAll();

    List<Book> findByIsbn(String isbn);
}
