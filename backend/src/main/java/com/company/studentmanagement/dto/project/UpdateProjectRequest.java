package com.company.studentmanagement.dto.project;

public record UpdateProjectRequest(
        String name,
        String description,
        String technologies,
        Integer progressPct,
        String status,
        String githubLink,
        String liveLink
) {}
