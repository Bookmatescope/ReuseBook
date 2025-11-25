package com.reusebook.cart.controller;

import com.reusebook.cart.dto.AddCartItemRequest;
import com.reusebook.cart.dto.CartItemResponse;
import com.reusebook.cart.dto.UpdateCartItemRequest;
import com.reusebook.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 购物车接口：提供增删改查能力
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItem(request));
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponse>> listItems(@RequestParam("buyerEmail") String buyerEmail) {
        return ResponseEntity.ok(cartService.listItems(buyerEmail));
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(@PathVariable UUID cartItemId,
                                                            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID cartItemId) {
        cartService.remove(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
