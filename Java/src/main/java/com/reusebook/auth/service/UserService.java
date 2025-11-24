package com.reusebook.auth.service;

import com.reusebook.auth.dto.AuthResponse;
import com.reusebook.auth.dto.LoginRequest;
import com.reusebook.auth.dto.RegisterRequest;
import com.reusebook.auth.dto.UserProfile;
import com.reusebook.common.exception.BusinessException;
import com.reusebook.user.model.UserAccount;
import com.reusebook.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public UserProfile register(RegisterRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new BusinessException(HttpStatus.CONFLICT, "该邮箱已注册");
        });
        UserAccount user = new UserAccount(
                UUID.randomUUID(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname(),
                Instant.now()
        );
        userRepository.save(user);
        return toProfile(user);
    }

    public AuthResponse login(LoginRequest request) {
        UserAccount user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "邮箱或密码错误"));
        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "邮箱或密码错误");
        }
        String token = tokenService.issueToken(user.email());
        return new AuthResponse(token, toProfile(user));
    }

    public UserProfile verify(String token) {
        TokenPayload payload;
        try {
            payload = tokenService.verify(token);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Token 无效");
        }
        UserAccount user = userRepository.findByEmail(payload.subject())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));
        return toProfile(user);
    }

    private UserProfile toProfile(UserAccount user) {
        return new UserProfile(user.id(), user.email(), user.nickname(), user.createdAt());
    }
}
