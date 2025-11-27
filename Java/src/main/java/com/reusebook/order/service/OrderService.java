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
     * 按状态查询用户订单
     */
    public List<OrderResponse> getOrdersByStatus(UUID userId, OrderStatus status) {
        return orderRepository.findByUserId(userId).stream()
                .filter(order -> order.status() == status)
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

    /**
     * 更新订单状态（面交流程）
     * PENDING -> CONFIRMED -> MEETUP -> COMPLETED
     * 任意状态 -> CANCELLED
     */
    public OrderResponse updateStatus(UUID userId, UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "订单不存在"));
        
        if (!order.userId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "无权操作此订单");
        }
        
        validateStatusTransition(order.status(), newStatus);
        
        Order updatedOrder = new Order(
                order.id(),
                order.userId(),
                order.items(),
                order.addressId(),
                order.totalAmount(),
                newStatus,
                order.createdAt()
        );
        orderRepository.save(updatedOrder);
        return toResponse(updatedOrder);
    }

    /**
     * 验证状态流转是否合法
     */
    private void validateStatusTransition(OrderStatus current, OrderStatus target) {
        // 取消订单始终允许（除非已完成或已取消）
        if (target == OrderStatus.CANCELLED) {
            if (current == OrderStatus.COMPLETED || current == OrderStatus.CANCELLED) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "当前状态不允许取消");
            }
            return;
        }
        
        // 正向流转验证
        boolean valid = switch (current) {
            case PENDING -> target == OrderStatus.CONFIRMED;
            case CONFIRMED -> target == OrderStatus.MEETUP;
            case MEETUP -> target == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
        
        if (!valid) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, 
                    "非法状态流转: " + current + " -> " + target);
        }
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
