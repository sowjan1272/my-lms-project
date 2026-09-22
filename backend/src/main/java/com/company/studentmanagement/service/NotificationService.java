package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.notification.NotificationDto;
import com.company.studentmanagement.entity.User;

public interface NotificationService {
    void notify(User user, String title, String body);
    PageResponse<NotificationDto> myNotifications(Long userId, int page, int size);
    long unreadCount(Long userId);
    void markRead(Long userId, Long notificationId);
}
