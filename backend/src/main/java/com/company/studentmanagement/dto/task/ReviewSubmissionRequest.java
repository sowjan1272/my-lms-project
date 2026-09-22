package com.company.studentmanagement.dto.task;

import jakarta.validation.constraints.NotBlank;

public record ReviewSubmissionRequest(
        @NotBlank String decision, // COMPLETED | NEEDS_REVISION
        String feedback
) {}
