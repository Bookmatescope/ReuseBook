package com.reusebook.cart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 购物车条目实体：映射数据库cart_items表
 */
@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_buyer", columnList = "buyer_email")
})
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @Column(name = "buyer_email", nullable = false, length = 100)
    private String buyerEmail;

    @Column(name = "book_title", nullable = false, length = 200)
    private String bookTitle;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    public CartItemEntity() {
    }

    public CartItemEntity(UUID id, UUID bookId, String buyerEmail, String bookTitle,
                          BigDecimal unitPrice, int quantity, Instant addedAt) {
        this.id = id;
        this.bookId = bookId;
        this.buyerEmail = buyerEmail;
        this.bookTitle = bookTitle;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = Instant.now();
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }
}
