package com.reusebook.auth.service;

import com.reusebook.auth.dto.LoginRequest;
import com.reusebook.auth.dto.RegisterRequest;
import com.reusebook.common.exception.BusinessException;
import com.reusebook.user.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserRepository(), new BCryptPasswordEncoder(), new TokenService());
    }

    @Test
    void should_register_and_login_successfully() {
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "Tester", "password123");
        var profile = userService.register(registerRequest);
        assertThat(profile.email()).isEqualTo(registerRequest.email());

        var response = userService.login(new LoginRequest("test@example.com", "password123"));
        assertThat(response.token()).isNotBlank();
        assertThat(response.profile().nickname()).isEqualTo("Tester");
    }

    @Test
    void should_fail_when_email_already_exists() {
        RegisterRequest registerRequest = new RegisterRequest("duplicate@example.com", "Tester", "password123");
        userService.register(registerRequest);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("该邮箱已注册");
    }
}
