package com.reusebook.user.repository;

import com.reusebook.user.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 地址JPA仓储接口
 */
@Repository
public interface JpaAddressRepository extends JpaRepository<AddressEntity, UUID> {
    
    List<AddressEntity> findByUserId(UUID userId);
    
    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.userId = :userId")
    void clearDefaultByUserId(UUID userId);
}
