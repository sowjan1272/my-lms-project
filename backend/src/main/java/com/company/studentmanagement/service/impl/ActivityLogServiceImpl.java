package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.entity.ActivityLog;
import com.company.studentmanagement.repository.ActivityLogRepository;
import com.company.studentmanagement.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Override
    public void log(Long userId, String action, String entityType, Long entityId) {
        activityLogRepository.save(new ActivityLog(userId, action, entityType, entityId));
    }
}
