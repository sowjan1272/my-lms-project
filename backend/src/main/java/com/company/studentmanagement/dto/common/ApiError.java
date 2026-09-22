package com.company.studentmanagement.dto.common;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String message,
        List<String> details
) {
    public static ApiError of(int status, String message) {
        return new ApiError(LocalDateTime.now(), status, message, List.of());
    }

    public static ApiError of(int status, String message, List<String> details) {
        return new ApiError(LocalDateTime.now(), status, message, details);
    }
}
