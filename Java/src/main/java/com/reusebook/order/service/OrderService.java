package com.reusebook.order.service;

import com.reusebook.book.dto.BookResponse;
import com.reusebook.book.service.BookService;
import com.reusebook.common.exception.BusinessException;
import com.reusebook.order.dto.*;
import com.reusebook.order.model.Order;
import com.reusebook.order.model.OrderItem;
import com.reusebook.order.model.OrderStatus;
import com.reusebook.order.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookService bookService;

    public OrderService(OrderRepository orderRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
    }

    /**
     * 创建订单
     */
    public OrderResponse create(UUID userId, CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(itemReq -> {
                    BookResponse book = bookService.findById(itemReq.bookId());
                    return new OrderItem(
                            book.id(),
                            book.title(),
                            book.price(),
                            itemReq.quantity()
                    );
                })
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        Order order = new Order(
                UUID.randomUUID(),
                userId,
                items,
                request.addressId(),
                totalAmount,
                OrderStatus.PENDING,
                Instant.now()
        );
        orderRepository.save(order);
        return toResponse(order);
    }

    /**
     * 获取用户订单列表
     */
    public List<OrderResponse> getOrders(UUID userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 获取订单详情
     */
    public OrderResponse getOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在"));
        if (!order.userId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "无权查看此订单");
        }
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.items().stream()
                .map(item -> new OrderItemResponse(
                        item.bookId(),
                        item.bookTitle(),
                        item.price(),
                        item.quantity()
                ))
                .toList();
        return new OrderResponse(
                order.id(),
                items,
                order.addressId(),
                order.totalAmount(),
                order.status(),
                order.createdAt()
        );
    }
}
