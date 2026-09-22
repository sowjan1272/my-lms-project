package com.company.studentmanagement.dto.task;

public record SubmitTaskRequest(
        String textResponse,
        String fileUrl,
        String githubLink,
        String demoLink
) {}
