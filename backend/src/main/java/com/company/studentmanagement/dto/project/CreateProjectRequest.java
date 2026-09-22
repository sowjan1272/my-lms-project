package com.company.studentmanagement.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateProjectRequest(
        @NotBlank String name,
        String description,
        String technologies,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        String githubLink,
        String liveLink,
        List<Long> staffIds,
        List<Long> studentIds
) {}
