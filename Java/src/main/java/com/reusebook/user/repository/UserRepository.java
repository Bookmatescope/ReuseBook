package com.reusebook.user.repository;

import com.reusebook.user.model.UserAccount;

import java.util.Optional;

/**
 * 用户仓储抽象：方便后续替换为数据库实现
 */
public interface UserRepository {

    UserAccount save(UserAccount userAccount);

    Optional<UserAccount> findByEmail(String email);
}
