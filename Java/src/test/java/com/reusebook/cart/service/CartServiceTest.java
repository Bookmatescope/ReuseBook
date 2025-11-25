package com.reusebook.cart.service;

import com.reusebook.cart.dto.AddCartItemRequest;
import com.reusebook.cart.dto.UpdateCartItemRequest;
import com.reusebook.cart.repository.InMemoryCartRepository;
import com.reusebook.book.model.Book;
import com.reusebook.book.repository.InMemoryBookRepository;
import com.reusebook.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 购物车服务测试：覆盖增删改查主流程
 */
class CartServiceTest {

    private CartService cartService;
    private InMemoryBookRepository bookRepository;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        var cartRepository = new InMemoryCartRepository();
        cartService = new CartService(cartRepository, bookRepository);
        Book book = new Book(
                UUID.randomUUID(),
                "9787302671491",
                "算法导论",
                "CLRS",
                "经典教材",
                new BigDecimal("35.00"),
                "九成新",
                "seller@reusebook.cn",
                Instant.now()
        );
        bookRepository.save(book);
        bookId = book.id();
    }

    @Test
    void should_add_item_and_list_by_buyer() {
        var response = cartService.addItem(new AddCartItemRequest(bookId, "buyer@reusebook.cn", 2));

        assertThat(response.quantity()).isEqualTo(2);
        assertThat(cartService.listItems("buyer@reusebook.cn")).hasSize(1);
    }

    @Test
    void should_accumulate_quantity_when_same_book_added_twice() {
        cartService.addItem(new AddCartItemRequest(bookId, "buyer@reusebook.cn", 1));
        var response = cartService.addItem(new AddCartItemRequest(bookId, "buyer@reusebook.cn", 3));

        assertThat(response.quantity()).isEqualTo(4);
    }

    @Test
    void should_update_quantity() {
        var created = cartService.addItem(new AddCartItemRequest(bookId, "buyer@reusebook.cn", 2));

        var updated = cartService.updateQuantity(created.id(), new UpdateCartItemRequest(5));

        assertThat(updated.quantity()).isEqualTo(5);
        assertThat(updated.subtotal()).isEqualByComparingTo("175.00");
    }

    @Test
    void should_remove_item() {
        var created = cartService.addItem(new AddCartItemRequest(bookId, "buyer@reusebook.cn", 1));

        cartService.remove(created.id());

        assertThat(cartService.listItems("buyer@reusebook.cn")).isEmpty();
    }

    @Test
    void should_throw_when_book_not_found() {
        assertThatThrownBy(() -> cartService.addItem(new AddCartItemRequest(UUID.randomUUID(), "buyer@reusebook.cn", 1)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("书籍不存在");
    }
}
