package com.reusebook.auth.service;

import com.reusebook.common.exception.BusinessException;
import com.reusebook.auth.support.TestUserData;
import com.reusebook.user.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 用户服务单测：覆盖注册/登录主流程与重复注册异常
 */
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserRepository(), new BCryptPasswordEncoder(), new TokenService());
    }

    @Test
    void should_register_and_login_successfully() {
        var profile = userService.register(TestUserData.registerRequest());
        assertThat(profile.email()).isEqualTo(TestUserData.email());

        var response = userService.login(TestUserData.loginRequest());
        assertThat(response.token()).isNotBlank();
        assertThat(response.profile().nickname()).isEqualTo("测试同学");
    }

    @Test
    void should_fail_when_email_already_exists() {
        var registerRequest = TestUserData.registerRequest("duplicate@example.com");
        userService.register(registerRequest);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("该邮箱已注册");
    }

    @Test
    void should_fail_when_password_incorrect() {
        userService.register(TestUserData.registerRequest());

        assertThatThrownBy(() -> userService.login(TestUserData.loginRequestWithPassword("wrong-pass")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("邮箱或密码错误");
    }

    @Test
    void should_verify_token_and_return_profile() {
        userService.register(TestUserData.registerRequest());
        var auth = userService.login(TestUserData.loginRequest());

        var profile = userService.verify(auth.token());

        assertThat(profile.email()).isEqualTo(TestUserData.email());
        assertThat(profile.nickname()).isEqualTo("测试同学");
    }

    @Test
    void should_fail_when_token_is_invalid() {
        userService.register(TestUserData.registerRequest());

        assertThatThrownBy(() -> userService.verify("invalid-token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Token 无效");
    }
}
