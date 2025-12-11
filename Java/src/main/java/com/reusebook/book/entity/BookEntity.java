package com.reusebook.book.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 书籍实体：映射数据库books表
 */
@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_books_isbn", columnList = "isbn"),
    @Index(name = "idx_books_seller", columnList = "seller_email")
})
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String isbn;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 100)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "book_condition", length = 20)
    private String condition;

    @Column(name = "seller_email", nullable = false, length = 100)
    private String sellerEmail;

    @Column(name = "meetup_location", nullable = false, length = 200)
    private String meetupLocation;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "is_sold", nullable = false)
    private boolean isSold = false;

    public BookEntity() {
    }

    public BookEntity(UUID id, String isbn, String title, String author, String description,
                      BigDecimal price, String condition, String sellerEmail, 
                      String meetupLocation, Instant createdAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.condition = condition;
        this.sellerEmail = sellerEmail;
        this.meetupLocation = meetupLocation;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getMeetupLocation() {
        return meetupLocation;
    }

    public void setMeetupLocation(String meetupLocation) {
        this.meetupLocation = meetupLocation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }
}
