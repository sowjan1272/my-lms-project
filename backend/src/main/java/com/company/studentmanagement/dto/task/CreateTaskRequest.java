package com.company.studentmanagement.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateTaskRequest(
        @NotBlank String title,
        @NotBlank String description,
        String category,
        String priority, // LOW MEDIUM HIGH URGENT
        @NotEmpty List<Long> studentIds,
        @NotNull LocalDate dueDate,
        String attachmentUrl
) {}
