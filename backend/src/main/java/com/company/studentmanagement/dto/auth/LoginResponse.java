package com.company.studentmanagement.dto.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        String role,
        String status,
        String fullName
) {}
