package com.company.studentmanagement.dto.enrollment;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EnrollStudentRequest(
        @NotNull Long studentId,
        @NotNull Long courseId,
        LocalDate startDate,
        BigDecimal totalFee
) {}
