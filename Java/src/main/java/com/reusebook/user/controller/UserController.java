package com.reusebook.user.controller;

import com.reusebook.auth.dto.UserProfile;
import com.reusebook.auth.service.TokenService;
import com.reusebook.user.dto.AddressRequest;
import com.reusebook.user.dto.AddressResponse;
import com.reusebook.user.dto.UpdateProfileRequest;
import com.reusebook.user.service.AddressService;
import com.reusebook.user.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 用户个人信息与收货地址接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final ProfileService profileService;
    private final AddressService addressService;
    private final TokenService tokenService;

    public UserController(ProfileService profileService, AddressService addressService, TokenService tokenService) {
        this.profileService = profileService;
        this.addressService = addressService;
        this.tokenService = tokenService;
    }

    // ========== 个人资料 ==========

    /**
     * 获取个人资料
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(@RequestHeader HttpHeaders headers) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    /**
     * 更新个人资料（昵称）
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfile> updateProfile(
            @RequestHeader HttpHeaders headers,
            @Valid @RequestBody UpdateProfileRequest request) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.ok(profileService.updateProfile(userId, request));
    }

    // ========== 收货地址 ==========

    /**
     * 获取用户所有收货地址
     */
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(@RequestHeader HttpHeaders headers) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.ok(addressService.getAddresses(userId));
    }

    /**
     * 添加收货地址
     */
    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @RequestHeader HttpHeaders headers,
            @Valid @RequestBody AddressRequest request) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(userId, request));
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @RequestHeader HttpHeaders headers,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressRequest request) {
        UUID userId = extractUserId(headers);
        return ResponseEntity.ok(addressService.updateAddress(userId, addressId, request));
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @RequestHeader HttpHeaders headers,
            @PathVariable UUID addressId) {
        UUID userId = extractUserId(headers);
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 从 Authorization 头提取用户ID
     */
    private UUID extractUserId(HttpHeaders headers) {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("缺少 Token");
        }
        String token = authHeader.substring("Bearer ".length());
        // TokenPayload 的 subject 是 email，需要通过 email 查找用户ID
        String email = tokenService.verify(token).subject();
        return profileService.getUserIdByEmail(email);
    }
}
