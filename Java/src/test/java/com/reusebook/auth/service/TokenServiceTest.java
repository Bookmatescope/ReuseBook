package com.reusebook.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Token 服务单测：验证签发/校验逻辑与异常分支
 */
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
    }

    @Test
    void should_issue_and_verify_token_successfully() {
        String token = tokenService.issueToken("tester@reusebook.cn");

        TokenPayload payload = tokenService.verify(token);

        assertThat(payload.subject()).isEqualTo("tester@reusebook.cn");
        assertThat(payload.issuedAt()).isNotNull();
    }

    @Test
    void should_reject_token_with_tampered_signature() {
        String token = tokenService.issueToken("tester@reusebook.cn");
        String tampered = token.substring(0, token.length() - 2) + "ab";

        assertThatThrownBy(() -> tokenService.verify(tampered))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("token 无效");
    }

    @Test
    void should_reject_token_with_invalid_structure() {
        assertThatThrownBy(() -> tokenService.verify("not-base64"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("token 无效");
    }
}
