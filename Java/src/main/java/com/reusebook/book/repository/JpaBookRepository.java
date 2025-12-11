package com.reusebook.book.repository;

import com.reusebook.book.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 书籍JPA仓储接口
 */
@Repository
public interface JpaBookRepository extends JpaRepository<BookEntity, UUID> {
    
    List<BookEntity> findByIsbn(String isbn);
    
    List<BookEntity> findBySellerEmail(String sellerEmail);
    
    @Query("SELECT b FROM BookEntity b WHERE b.isSold = false ORDER BY b.createdAt DESC")
    List<BookEntity> findAllAvailable();
    
    @Query("SELECT b FROM BookEntity b WHERE b.isSold = false AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<BookEntity> searchByKeyword(String keyword);
}
