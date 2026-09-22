package com.company.studentmanagement.dto.task;

import java.time.LocalDateTime;

public record TaskSubmissionDto(
        Long id,
        String textResponse,
        String fileUrl,
        String githubLink,
        String demoLink,
        LocalDateTime submittedAt,
        String staffFeedback,
        LocalDateTime reviewedAt
) {}
