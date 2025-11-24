package com.reusebook.user.repository;

import com.reusebook.user.model.UserAccount;

import java.util.Optional;

public interface UserRepository {

    UserAccount save(UserAccount userAccount);

    Optional<UserAccount> findByEmail(String email);
}
