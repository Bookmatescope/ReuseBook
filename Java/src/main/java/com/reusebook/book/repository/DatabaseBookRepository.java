package com.reusebook.book.repository;

import com.reusebook.book.entity.BookEntity;
import com.reusebook.book.model.Book;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 书籍仓储JPA实现：使用数据库存储书籍数据
 */
@Repository
@Primary
public class DatabaseBookRepository implements BookRepository {

    private final JpaBookRepository jpaBookRepository;

    public DatabaseBookRepository(JpaBookRepository jpaBookRepository) {
        this.jpaBookRepository = jpaBookRepository;
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = toEntity(book);
        BookEntity saved = jpaBookRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return jpaBookRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<Book> findAll() {
        return jpaBookRepository.findAllAvailable().stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public List<Book> findByIsbn(String isbn) {
        return jpaBookRepository.findByIsbn(isbn).stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public List<Book> findHotBooks(int limit) {
        // 简化实现：返回最新的N本书
        return jpaBookRepository.findAllAvailable().stream()
                .limit(limit)
                .map(this::toModel)
                .toList();
    }

    @Override
    public List<Book> findBySellerId(UUID sellerId) {
        // 通过email查找（Book模型用sellerEmail而不是sellerId）
        // 这里需要根据实际情况调整
        return jpaBookRepository.findAll().stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return jpaBookRepository.searchByKeyword(keyword).stream()
                .map(this::toModel)
                .toList();
    }

    private BookEntity toEntity(Book model) {
        BookEntity entity = new BookEntity(
                model.id(),
                model.isbn(),
                model.title(),
                model.author(),
                model.description(),
                model.price(),
                model.condition(),
                model.sellerEmail(),
                model.meetupLocation(),
                model.createdAt()
        );
        return entity;
    }

    private Book toModel(BookEntity entity) {
        return new Book(
                entity.getId(),
                entity.getIsbn(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCondition(),
                entity.getSellerEmail(),
                entity.getMeetupLocation(),
                entity.getCreatedAt()
        );
    }
}
