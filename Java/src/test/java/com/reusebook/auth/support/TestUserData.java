package com.reusebook.auth.support;

import com.reusebook.auth.dto.LoginRequest;
import com.reusebook.auth.dto.RegisterRequest;

/**
 * 测试夹具：集中管理用户模块常用的输入数据
 */
public final class TestUserData {

    private static final String DEFAULT_EMAIL = "tester@reusebook.cn";
    private static final String DEFAULT_NICKNAME = "测试同学";
    private static final String DEFAULT_PASSWORD = "Password#123";

    private TestUserData() {
    }

    public static RegisterRequest registerRequest() {
        return new RegisterRequest(DEFAULT_EMAIL, DEFAULT_NICKNAME, DEFAULT_PASSWORD);
    }

    public static RegisterRequest registerRequest(String email) {
        return new RegisterRequest(email, DEFAULT_NICKNAME, DEFAULT_PASSWORD);
    }

    public static LoginRequest loginRequest() {
        return new LoginRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static LoginRequest loginRequestWithPassword(String password) {
        return new LoginRequest(DEFAULT_EMAIL, password);
    }

    public static String email() {
        return DEFAULT_EMAIL;
    }

    public static String password() {
        return DEFAULT_PASSWORD;
    }
}
