package com.company.studentmanagement.dto.notification;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String title,
        String body,
        boolean read,
        LocalDateTime createdAt
) {}
