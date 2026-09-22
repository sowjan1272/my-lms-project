package com.company.studentmanagement.dto.staff;

import java.math.BigDecimal;
import java.util.List;

public record StaffSummaryDto(
        Long id,
        String staffCode,
        String fullName,
        String photoUrl,
        String email,
        String department,
        String designation,
        boolean active,
        BigDecimal salaryBase,
        List<String> skills
) {}
