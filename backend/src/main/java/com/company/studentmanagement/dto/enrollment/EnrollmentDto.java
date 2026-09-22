package com.company.studentmanagement.dto.enrollment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EnrollmentDto(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseName,
        String courseType,
        LocalDate startDate,
        LocalDate endDate,
        Integer progressPct,
        BigDecimal totalFee,
        LocalDateTime createdAt
) {}
