package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.notification.NotificationDto;
import com.company.studentmanagement.entity.Notification;
import com.company.studentmanagement.entity.User;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.NotificationRepository;
import com.company.studentmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void notify(User user, String title, String body) {
        notificationRepository.save(new Notification(user, title, body));
    }

    @Override
    public PageResponse<NotificationDto> myNotifications(Long userId, int page, int size) {
        var result = notificationRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return PageResponse.from(result.map(this::toDto));
    }

    @Override
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public void markRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ApiException.notFound("Notification not found"));
        if (!n.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("You cannot modify another user's notification.");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    private NotificationDto toDto(Notification n) {
        return new NotificationDto(n.getId(), n.getTitle(), n.getBody(), n.isRead(), n.getCreatedAt());
    }
}
