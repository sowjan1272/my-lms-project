package com.company.studentmanagement.dto.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskDto(
        Long id,
        String title,
        String description,
        String category,
        String priority,
        String status,
        Long assignedByStaffId,
        String assignedByName,
        String assignedByPhotoUrl,
        Long studentId,
        String studentName,
        LocalDate dueDate,
        String attachmentUrl,
        LocalDateTime createdAt
) {}
