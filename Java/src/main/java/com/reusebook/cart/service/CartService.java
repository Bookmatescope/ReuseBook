package com.reusebook.cart.service;

import com.reusebook.cart.dto.AddCartItemRequest;
import com.reusebook.cart.dto.CartItemResponse;
import com.reusebook.cart.dto.UpdateCartItemRequest;
import com.reusebook.cart.model.CartItem;
import com.reusebook.cart.repository.CartRepository;
import com.reusebook.book.model.Book;
import com.reusebook.book.repository.BookRepository;
import com.reusebook.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 购物车服务：封装增删改查与业务校验
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    public CartService(CartRepository cartRepository, BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
    }

    public CartItemResponse addItem(AddCartItemRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "书籍不存在，请刷新后重试"));
        String buyerEmail = normalizeEmail(request.buyerEmail());
        CartItem existing = cartRepository.findByBuyerAndBook(buyerEmail, request.bookId()).orElse(null);
        CartItem item;
        if (existing != null) {
            item = new CartItem(
                    existing.id(),
                    existing.bookId(),
                    existing.buyerEmail(),
                    existing.bookTitle(),
                    existing.unitPrice(),
                    existing.quantity() + request.quantity(),
                    existing.addedAt()
            );
        } else {
            item = new CartItem(
                    UUID.randomUUID(),
                    book.id(),
                    buyerEmail,
                    book.title(),
                    book.price(),
                    request.quantity(),
                    Instant.now()
            );
        }
        cartRepository.save(item);
        return toResponse(item);
    }

    public List<CartItemResponse> listItems(String buyerEmail) {
        if (!StringUtils.hasText(buyerEmail)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "缺少买家邮箱");
        }
        return cartRepository.findByBuyer(buyerEmail).stream()
                .map(this::toResponse)
                .toList();
    }

    public CartItemResponse updateQuantity(UUID cartItemId, UpdateCartItemRequest request) {
        CartItem existing = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "购物车条目不存在"));
        CartItem updated = new CartItem(
                existing.id(),
                existing.bookId(),
                existing.buyerEmail(),
                existing.bookTitle(),
                existing.unitPrice(),
                request.quantity(),
                existing.addedAt()
        );
        cartRepository.save(updated);
        return toResponse(updated);
    }

    public void remove(UUID cartItemId) {
        cartRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "购物车条目不存在"));
        cartRepository.delete(cartItemId);
    }

    private CartItemResponse toResponse(CartItem item) {
        BigDecimal subtotal = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()))
                .setScale(2, RoundingMode.HALF_UP);
        return new CartItemResponse(
                item.id(),
                item.bookId(),
                item.bookTitle(),
                item.buyerEmail(),
                item.unitPrice().setScale(2, RoundingMode.HALF_UP),
                item.quantity(),
                subtotal,
                item.addedAt()
        );
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "买家邮箱不能为空");
        }
        return email.trim().toLowerCase();
    }
}
