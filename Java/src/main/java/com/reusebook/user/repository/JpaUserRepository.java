package com.reusebook.user.repository;

import com.reusebook.user.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 用户JPA仓储接口
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserAccountEntity, UUID> {
    
    Optional<UserAccountEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
