package com.company.studentmanagement.dto.course;

import java.math.BigDecimal;
import java.util.List;

public record CourseDto(
        Long id,
        String name,
        String description,
        String type,
        Integer durationMonths,
        BigDecimal fee,
        Long mentorId,
        String mentorName,
        boolean active,
        List<String> modules,
        long enrolledCount
) {}
