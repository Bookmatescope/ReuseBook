package com.reusebook.user.repository;

import com.reusebook.user.entity.AddressEntity;
import com.reusebook.user.model.Address;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 地址仓储JPA实现：使用数据库存储地址数据
 */
@Repository
@Primary
public class DatabaseAddressRepository implements AddressRepository {

    private final JpaAddressRepository jpaAddressRepository;

    public DatabaseAddressRepository(JpaAddressRepository jpaAddressRepository) {
        this.jpaAddressRepository = jpaAddressRepository;
    }

    @Override
    @Transactional
    public Address save(Address address) {
        // 如果设为默认地址，先清除该用户的其他默认地址
        if (address.isDefault()) {
            jpaAddressRepository.clearDefaultByUserId(address.userId());
        }
        AddressEntity entity = toEntity(address);
        AddressEntity saved = jpaAddressRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Address> findById(UUID id) {
        return jpaAddressRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<Address> findByUserId(UUID userId) {
        return jpaAddressRepository.findByUserId(userId).stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaAddressRepository.deleteById(id);
    }

    private AddressEntity toEntity(Address model) {
        return new AddressEntity(
                model.id(),
                model.userId(),
                model.recipientName(),
                model.phone(),
                model.province(),
                model.city(),
                model.district(),
                model.detailAddress(),
                model.isDefault()
        );
    }

    private Address toModel(AddressEntity entity) {
        return new Address(
                entity.getId(),
                entity.getUserId(),
                entity.getRecipientName(),
                entity.getPhone(),
                entity.getProvince(),
                entity.getCity(),
                entity.getDistrict(),
                entity.getDetailAddress(),
                entity.isDefault()
        );
    }
}
