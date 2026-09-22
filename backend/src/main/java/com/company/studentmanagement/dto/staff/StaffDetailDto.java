package com.company.studentmanagement.dto.staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record StaffDetailDto(
        Long id,
        String staffCode,
        String fullName,
        String photoUrl,
        String email,
        String phone,
        String address,
        LocalDate dateOfJoining,
        String designation,
        String department,
        String qualification,
        BigDecimal salaryBase,
        boolean active,
        List<String> skills
) {}
