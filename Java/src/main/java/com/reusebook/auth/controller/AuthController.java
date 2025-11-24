package com.reusebook.auth.controller;

import com.reusebook.auth.dto.AuthResponse;
import com.reusebook.auth.dto.LoginRequest;
import com.reusebook.auth.dto.RegisterRequest;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfile> register(@Valid @RequestBody RegisterRequest request) {
        UserProfile profile = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> profile(@RequestHeader HttpHeaders headers) {
        String token = resolveToken(headers.getFirst(HttpHeaders.AUTHORIZATION));
        return ResponseEntity.ok(userService.verify(token));
    }

    private String resolveToken(String headerValue) {
        if (headerValue == null || !headerValue.startsWith("Bearer ")) {
            throw new IllegalArgumentException("缺少 Token");
        }
        return headerValue.substring("Bearer ".length());
    }
}
