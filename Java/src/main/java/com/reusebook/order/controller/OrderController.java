package com.reusebook.order.controller;

import com.reusebook.auth.service.TokenService;
import com.reusebook.order.dto.CreateOrderRequest;
import com.reusebook.order.dto.OrderResponse;
import com.reusebook.order.dto.UpdateOrderStatusRequest;
import com.reusebook.order.model.OrderStatus;
import com.reusebook.order.service.OrderService;
import com.reusebook.user.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final TokenService tokenService;
    private final ProfileService profileService;

    public OrderController(OrderService orderService, TokenService tokenService, ProfileService profileService) {
        this.orderService = orderService;
        this.tokenService = tokenService;
        this.profileService = profileService;
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestHeader HttpHeaders headers,
            @Valid @RequestBody CreateOrderRequest request) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(userId, request));
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> list(
            @RequestHeader HttpHeaders headers,
            @RequestParam(required = false) OrderStatus status) {
        UUID userId = extractUserId(headers);
        if (status != null) {
            return ResponseEntity.ok(orderService.getOrdersByStatus(userId, status));
        }
        return ResponseEntity.ok(orderService.getOrders(userId));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> detail(
            @RequestHeader HttpHeaders headers,
            @PathVariable UUID orderId) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }

    /**
     * 更新订单状态（面交流程）
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @RequestHeader HttpHeaders headers,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.ok(orderService.updateStatus(userId, orderId, request.status()));
    }

    private UUID extractUserId(HttpHeaders headers) {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("缺少 Token");
        }
        String token = authHeader.substring("Bearer ".length());
        String email = tokenService.verify(token).subject();
        return profileService.getUserIdByEmail(email);
    }
}
