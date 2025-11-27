package com.reusebook.order.service;

import com.reusebook.book.model.Book;
import com.reusebook.book.repository.InMemoryBookRepository;
import com.reusebook.book.service.BookService;
import com.reusebook.book.service.IsbnLookupService;
import com.reusebook.common.exception.BusinessException;
import com.reusebook.order.dto.CreateOrderRequest;
import com.reusebook.order.dto.OrderItemRequest;
import com.reusebook.order.model.OrderStatus;
import com.reusebook.order.repository.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 订单服务测试：覆盖订单创建与面交流程
 */
class OrderServiceTest {

    private OrderService orderService;
    private InMemoryBookRepository bookRepository;
    private UUID bookId1;
    private UUID bookId2;
    private UUID buyerId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        var orderRepository = new InMemoryOrderRepository();
        var bookService = new BookService(bookRepository, new IsbnLookupService());
        orderService = new OrderService(orderRepository, bookService);

        // 创建测试书籍（含面交地址）
        Book book1 = new Book(
                UUID.randomUUID(),
                "9787115428028",
                "深入理解计算机系统",
                "Randal E.Bryant",
                "经典教材",
                new BigDecimal("45.00"),
                "九成新",
                "seller1@reusebook.cn",
                "图书馆一楼大厅",
                Instant.now()
        );
        Book book2 = new Book(
                UUID.randomUUID(),
                "9787111544937",
                "算法导论",
                "CLRS",
                "算法经典",
                new BigDecimal("60.00"),
                "全新",
                "seller2@reusebook.cn",
                "西门广场",
                Instant.now()
        );
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookId1 = book1.id();
        bookId2 = book2.id();
        buyerId = UUID.randomUUID();
        addressId = UUID.randomUUID();
    }

    @Test
    void should_create_order_with_pending_status() {
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 1)),
                addressId
        );

        var response = orderService.create(buyerId, request);

        assertThat(response.id()).isNotNull();
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.items()).hasSize(1);
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("45.00"));
    }

    @Test
    void should_calculate_total_amount_correctly() {
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new OrderItemRequest(bookId1, 2),  // 45.00 * 2 = 90.00
                        new OrderItemRequest(bookId2, 1)   // 60.00 * 1 = 60.00
                ),
                addressId
        );

        var response = orderService.create(buyerId, request);

        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    void should_list_user_orders() {
        // 创建两个订单
        orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 1)), addressId));
        orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId2, 1)), addressId));

        var orders = orderService.getOrders(buyerId);

        assertThat(orders).hasSize(2);
    }

    @Test
    void should_get_order_detail() {
        var created = orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 1)), addressId));

        var found = orderService.getOrder(buyerId, created.id());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.items()).hasSize(1);
        assertThat(found.items().get(0).bookTitle()).isEqualTo("深入理解计算机系统");
    }

    @Test
    void should_throw_when_order_not_found() {
        UUID randomOrderId = UUID.randomUUID();

        assertThatThrownBy(() -> orderService.getOrder(buyerId, randomOrderId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("订单不存在");
    }

    @Test
    void should_throw_when_accessing_others_order() {
        var created = orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 1)), addressId));

        UUID anotherUserId = UUID.randomUUID();
        assertThatThrownBy(() -> orderService.getOrder(anotherUserId, created.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("无权查看此订单");
    }

    @Test
    void should_not_list_other_users_orders() {
        orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 1)), addressId));

        UUID anotherUserId = UUID.randomUUID();
        var otherOrders = orderService.getOrders(anotherUserId);

        assertThat(otherOrders).isEmpty();
    }

    @Test
    void should_preserve_order_items_info() {
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 3)),
                addressId
        );

        var response = orderService.create(buyerId, request);
        var item = response.items().get(0);

        assertThat(item.bookId()).isEqualTo(bookId1);
        assertThat(item.quantity()).isEqualTo(3);
        assertThat(item.price()).isEqualByComparingTo(new BigDecimal("45.00"));
    }

    @Test
    void should_have_created_at_timestamp() {
        var before = Instant.now().minusSeconds(1);
        
        var response = orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 1)), addressId));

        assertThat(response.createdAt()).isAfter(before);
    }

    @Test
    void should_include_address_id_in_order() {
        var response = orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 1)), addressId));

        assertThat(response.addressId()).isEqualTo(addressId);
    }
}
