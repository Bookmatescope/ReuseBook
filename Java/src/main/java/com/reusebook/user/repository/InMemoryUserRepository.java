package com.reusebook.user.repository;

import com.reusebook.user.model.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存实现：用于 Alpha 阶段快速验证流程
 */
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, UserAccount> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public UserAccount save(UserAccount userAccount) {
        usersByEmail.put(userAccount.email().toLowerCase(), userAccount);
        return userAccount;
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
    }
}
