package com.reusebook.user.repository;

import com.reusebook.user.entity.UserAccountEntity;
import com.reusebook.user.model.UserAccount;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 用户仓储JPA实现：使用数据库存储用户数据
 */
@Repository
@Primary
public class DatabaseUserRepository implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public DatabaseUserRepository(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        UserAccountEntity entity = toEntity(userAccount);
        UserAccountEntity saved = jpaUserRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(this::toModel);
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        return jpaUserRepository.findById(id).map(this::toModel);
    }

    private UserAccountEntity toEntity(UserAccount model) {
        return new UserAccountEntity(
                model.id(),
                model.email(),
                model.passwordHash(),
                model.nickname(),
                model.createdAt()
        );
    }

    private UserAccount toModel(UserAccountEntity entity) {
        return new UserAccount(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getNickname(),
                entity.getCreatedAt()
        );
    }
}
