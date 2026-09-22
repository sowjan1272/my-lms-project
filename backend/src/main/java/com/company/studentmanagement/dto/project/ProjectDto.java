package com.company.studentmanagement.dto.project;

import java.time.LocalDate;
import java.util.List;

public record ProjectDto(
        Long id,
        String name,
        String description,
        String technologies,
        LocalDate startDate,
        LocalDate endDate,
        Integer progressPct,
        String status,
        String githubLink,
        String liveLink,
        List<String> memberNames
) {}
