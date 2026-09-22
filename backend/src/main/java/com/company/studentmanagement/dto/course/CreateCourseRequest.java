package com.company.studentmanagement.dto.course;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateCourseRequest(
        @NotBlank String name,
        String description,
        @NotBlank String type, // COURSE | INTERNSHIP
        @NotNull @Positive Integer durationMonths,
        @NotNull @PositiveOrZero BigDecimal fee,
        Long mentorId,
        List<String> modules
) {}
