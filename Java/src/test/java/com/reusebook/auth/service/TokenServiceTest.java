package com.reusebook.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

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
        assertThat(payload.expiresAt()).isAfter(payload.issuedAt());
    }

    @Test
    void should_reject_token_with_tampered_signature() {
        String token = tokenService.issueToken("tester@reusebook.cn");
        String tampered = token.substring(0, token.length() - 2) + "ab";

        assertThatThrownBy(() -> tokenService.verify(tampered))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("signature");
    }

    @Test
    void should_reject_token_with_invalid_structure() {
        assertThatThrownBy(() -> tokenService.verify("not-base64"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("structure");
    }

    @Test
    void should_reject_expired_token() {
        long now = Instant.now().getEpochSecond();
        String expired = buildCustomToken("tester@reusebook.cn", now - 10, now - 5);

        assertThatThrownBy(() -> tokenService.verify(expired))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("token 已过期");
    }

    @Test
    void should_refresh_token_with_new_expiry() {
        String token = tokenService.issueToken("tester@reusebook.cn");
        TokenPayload original = tokenService.verify(token);

        String refreshed = tokenService.refresh(token);
        TokenPayload refreshedPayload = tokenService.verify(refreshed);

        assertThat(refreshedPayload.issuedAt()).isAfterOrEqualTo(original.issuedAt());
        assertThat(refreshedPayload.expiresAt()).isAfterOrEqualTo(original.expiresAt());
        assertThat(refreshedPayload.subject()).isEqualTo("tester@reusebook.cn");
    }

    private String buildCustomToken(String subject, long issuedAt, long expiresAt) {
        try {
            Method signMethod = TokenService.class.getDeclaredMethod("sign", String.class);
            signMethod.setAccessible(true);
            String payload = subject + ":" + issuedAt + ":" + expiresAt;
            String signature = (String) signMethod.invoke(tokenService, payload);
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString((payload + ":" + signature).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("failed to build custom token", e);
        }
    }
}
