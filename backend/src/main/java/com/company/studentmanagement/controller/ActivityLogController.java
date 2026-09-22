package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.entity.ActivityLog;
import com.company.studentmanagement.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity-log")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<ActivityLog> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return PageResponse.from(activityLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size)));
    }
}
