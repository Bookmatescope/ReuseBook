package com.reusebook.auth.service;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;

/**
 * 自定义 Token 服务：负责签发与校验简单的 HMAC Token
 */
@Component
public class TokenService {

    private static final String ALGORITHM = "HmacSHA256";
    private static final String SECRET = "reusebook-alpha-day1-demo-secret";
    private static final long EXPIRES_IN_SECONDS = 7 * 24 * 60 * 60;

    /**
     * 签发 Token：将 subject 与签发时间组合后加签
     */
    public String issueToken(String subject) {
        long issuedEpoch = Instant.now().getEpochSecond();
        long expiresEpoch = issuedEpoch + EXPIRES_IN_SECONDS;
        String payload = subject + ":" + issuedEpoch + ":" + expiresEpoch;
        String signature = sign(payload);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((payload + ":" + signature).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 校验 Token：检查结构、签名并返回载荷
     */
    public TokenPayload verify(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            if (parts.length != 4) {
                throw new IllegalArgumentException("invalid token structure");
            }
            long expiresEpoch = Long.parseLong(parts[2]);
            if (Instant.now().getEpochSecond() >= expiresEpoch) {
                throw new IllegalArgumentException("token 已过期");
            }
            String payload = parts[0] + ":" + parts[1] + ":" + parts[2];
            String expectedSignature = sign(payload);
            if (!expectedSignature.equals(parts[3])) {
                throw new IllegalArgumentException("invalid token signature");
            }
            return new TokenPayload(parts[0],
                    Instant.ofEpochSecond(Long.parseLong(parts[1])),
                    Instant.ofEpochSecond(expiresEpoch));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    /**
     * 刷新 Token：校验后重新签发
     */
    public String refresh(String token) {
        TokenPayload payload = verify(token);
        return issueToken(payload.subject());
    }

    /**
     * HMAC-SHA256 签名工具方法
     */
    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            byte[] signature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("token 服务不可用", e);
        }
    }
}
