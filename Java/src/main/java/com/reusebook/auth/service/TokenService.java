package com.reusebook.auth.service;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;

@Component
public class TokenService {

    private static final String ALGORITHM = "HmacSHA256";
    private static final String SECRET = "reusebook-alpha-day1-demo-secret";

    public String issueToken(String subject) {
        long issuedEpoch = Instant.now().getEpochSecond();
        String payload = subject + ":" + issuedEpoch;
        String signature = sign(payload);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((payload + ":" + signature).getBytes(StandardCharsets.UTF_8));
    }

    public TokenPayload verify(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("invalid token structure");
            }
            String payload = parts[0] + ":" + parts[1];
            String expectedSignature = sign(payload);
            if (!expectedSignature.equals(parts[2])) {
                throw new IllegalArgumentException("invalid token signature");
            }
            return new TokenPayload(parts[0], Instant.ofEpochSecond(Long.parseLong(parts[1])));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("token 无效", ex);
        }
    }

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
