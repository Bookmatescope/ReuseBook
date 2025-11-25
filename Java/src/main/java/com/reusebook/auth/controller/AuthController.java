package com.reusebook.auth.controller;

import com.reusebook.auth.dto.AuthResponse;
import com.reusebook.auth.dto.LoginRequest;
import com.reusebook.auth.dto.RegisterRequest;
import com.reusebook.auth.dto.RefreshTokenRequest;
import com.reusebook.auth.dto.UserProfile;
import com.reusebook.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：暴露注册、登录与资料查询端点
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册接口：返回创建后的用户基础信息
     */
    @PostMapping("/register")
    public ResponseEntity<UserProfile> register(@Valid @RequestBody RegisterRequest request) {
        UserProfile profile = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    /**
     * 登录接口：返回 token + profile
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    /**
     * 刷新 Token：延长登录状态
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refresh(request.token()));
    }

    /**
     * 资料接口：读取 Authorization 头并校验 token
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfile> profile(@RequestHeader HttpHeaders headers) {
        String token = resolveToken(headers.getFirst(HttpHeaders.AUTHORIZATION));
        return ResponseEntity.ok(userService.verify(token));
    }

    /**
     * 解析 Authorization Bearer 头
     */
    private String resolveToken(String headerValue) {
        if (headerValue == null || !headerValue.startsWith("Bearer ")) {
            throw new IllegalArgumentException("缺少 Token");
        }
        return headerValue.substring("Bearer ".length());
    }
}
