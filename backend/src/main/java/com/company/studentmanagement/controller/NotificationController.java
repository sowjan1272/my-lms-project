package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.notification.NotificationDto;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserProvider currentUser;

    @GetMapping
    public PageResponse<NotificationDto> mine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return notificationService.myNotifications(currentUser.userId(), page, size);
    }

    @GetMapping("/unread-count")
    public long unreadCount() {
        return notificationService.unreadCount(currentUser.userId());
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        notificationService.markRead(currentUser.userId(), id);
    }
}
