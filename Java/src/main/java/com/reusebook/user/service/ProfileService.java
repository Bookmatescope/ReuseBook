package com.reusebook.user.service;

import com.reusebook.auth.dto.UserProfile;
import com.reusebook.user.dto.UpdateProfileRequest;
import com.reusebook.user.model.UserAccount;
import com.reusebook.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户个人信息服务
 */
@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据 email 获取用户ID
     */
    public UUID getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"))
                .id();
    }

    /**
     * 根据用户ID获取个人资料
     */
    public UserProfile getProfile(UUID userId) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return new UserProfile(user.id(), user.email(), user.nickname(), user.createdAt());
    }

    /**
     * 更新用户昵称
     */
    public UserProfile updateProfile(UUID userId, UpdateProfileRequest request) {
        UserAccount existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        UserAccount updatedUser = new UserAccount(
                existingUser.id(),
                existingUser.email(),
                existingUser.passwordHash(),
                request.nickname(),
                existingUser.createdAt()
        );
        userRepository.save(updatedUser);
        return new UserProfile(updatedUser.id(), updatedUser.email(), updatedUser.nickname(), updatedUser.createdAt());
    }
}
