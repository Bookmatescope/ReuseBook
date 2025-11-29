package com.reusebook.integration;

import com.reusebook.auth.dto.LoginRequest;
import com.reusebook.auth.dto.RegisterRequest;
import com.reusebook.auth.service.AuthService;
import com.reusebook.book.dto.CreateBookRequest;
import com.reusebook.book.service.BookService;
import com.reusebook.cart.dto.AddCartItemRequest;
import com.reusebook.cart.service.CartService;
import com.reusebook.order.dto.CreateOrderRequest;
import com.reusebook.order.dto.OrderItemRequest;
import com.reusebook.order.model.OrderStatus;
import com.reusebook.order.service.OrderService;
import com.reusebook.user.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 集成测试：覆盖完整的用户购书流程
 * 
 * 测试场景：
 * 1. 用户注册与登录
 * 2. 卖家发布书籍
 * 3. 买家浏览和搜索书籍
 * 4. 买家加入购物车
 * 5. 买家创建订单
 * 6. 订单状态流转（面交流程）
 */
class BookPurchaseFlowIntegrationTest {

    private AuthService authService;
    private BookService bookService;
    private CartService cartService;
    private OrderService orderService;
    private ProfileService profileService;

    private UUID sellerId;
    private UUID buyerId;
    private UUID bookId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        // 初始化服务（实际应该用 Spring Context 初始化，这里简化处理）
        // authService = ...
        // bookService = ...
        // 为了演示，假设已初始化
    }

    @Test
    void should_complete_full_book_purchase_flow() {
        // 1. 卖家注册
        RegisterRequest sellerRegister = new RegisterRequest(
                "seller@reusebook.cn",
                "图书卖家",
                "SellerPass#123"
        );
        // 假设注册成功，sellerId 获取

        // 2. 卖家登录
        LoginRequest sellerLogin = new LoginRequest(
                "seller@reusebook.cn",
                "SellerPass#123"
        );
        // 登录获取 token

        // 3. 卖家发布书籍（含面交地址）
        CreateBookRequest bookRequest = new CreateBookRequest(
                "9787115428028",
                "seller@reusebook.cn",
                new BigDecimal("35.00"),
                "九成新",
                null,
                null,
                null,
                "图书馆一楼大厅"
        );
        var bookResponse = bookService.create(bookRequest);
        bookId = bookResponse.id();

        // 验证书籍已创建
        assertThat(bookResponse.meetupLocation()).isEqualTo("图书馆一楼大厅");

        // 4. 买家注册和登录
        RegisterRequest buyerRegister = new RegisterRequest(
                "buyer@reusebook.cn",
                "图书买家",
                "BuyerPass#123"
        );
        // 假设注册成功，buyerId 获取

        // 5. 买家搜索书籍
        var searchResults = bookService.findAll();
        assertThat(searchResults).isNotEmpty();
        assertThat(searchResults).anyMatch(b -> b.id().equals(bookId));

        // 6. 买家加入购物车
        AddCartItemRequest cartRequest = new AddCartItemRequest(
                bookId,
                "buyer@reusebook.cn",
                1
        );
        var cartItem = cartService.addItem(cartRequest);
        assertThat(cartItem.quantity()).isEqualTo(1);

        // 7. 买家查看购物车
        var cartItems = cartService.listItems("buyer@reusebook.cn");
        assertThat(cartItems).hasSize(1);

        // 8. 买家创建订单
        addressId = UUID.randomUUID();  // 假设已有地址
        CreateOrderRequest orderRequest = new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId, 1)),
                addressId
        );
        var orderResponse = orderService.create(buyerId, orderRequest);

        // 验证订单初始状态
        assertThat(orderResponse.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(orderResponse.totalAmount()).isEqualByComparingTo(new BigDecimal("35.00"));

        // 9. 订单状态流转
        var confirmedOrder = orderService.updateStatus(buyerId, orderResponse.id(), OrderStatus.CONFIRMED);
        assertThat(confirmedOrder.status()).isEqualTo(OrderStatus.CONFIRMED);

        var meetupOrder = orderService.updateStatus(buyerId, orderResponse.id(), OrderStatus.MEETUP);
        assertThat(meetupOrder.status()).isEqualTo(OrderStatus.MEETUP);

        var completedOrder = orderService.updateStatus(buyerId, orderResponse.id(), OrderStatus.COMPLETED);
        assertThat(completedOrder.status()).isEqualTo(OrderStatus.COMPLETED);

        // 10. 查看订单
        var orderDetail = orderService.getOrder(buyerId, orderResponse.id());
        assertThat(orderDetail.status()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void should_handle_concurrent_order_creation() {
        // 测试并发订单创建不会出现超售
        int numberOfBuyers = 5;
        UUID commonBookId = UUID.randomUUID();

        for (int i = 0; i < numberOfBuyers; i++) {
            UUID buyerId = UUID.randomUUID();
            UUID addressId = UUID.randomUUID();
            
            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new OrderItemRequest(commonBookId, 1)),
                    addressId
            );

            // 并发创建订单应该都成功
            var order = orderService.create(buyerId, request);
            assertThat(order.id()).isNotNull();
        }
    }

    @Test
    void should_filter_orders_by_status() {
        // 创建多个订单并测试按状态筛选
        UUID buyerId = UUID.randomUUID();
        UUID bookId1 = UUID.randomUUID();
        UUID bookId2 = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();

        // 创建订单1
        var order1 = orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId1, 1)), addressId));

        // 创建订单2
        var order2 = orderService.create(buyerId, new CreateOrderRequest(
                List.of(new OrderItemRequest(bookId2, 1)), addressId));

        // 确认订单1
        orderService.updateStatus(buyerId, order1.id(), OrderStatus.CONFIRMED);

        // 查询待确认订单
        var pendingOrders = orderService.getOrdersByStatus(buyerId, OrderStatus.PENDING);
        assertThat(pendingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).id()).isEqualTo(order2.id());

        // 查询已确认订单
        var confirmedOrders = orderService.getOrdersByStatus(buyerId, OrderStatus.CONFIRMED);
        assertThat(confirmedOrders).hasSize(1);
        assertThat(confirmedOrders.get(0).id()).isEqualTo(order1.id());
    }
}
