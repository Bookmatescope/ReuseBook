package com.reusebook.auth.dto;

public record AuthResponse(String token, UserProfile profile) {
}
