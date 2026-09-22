package com.company.studentmanagement.dto.staff;

import java.math.BigDecimal;
import java.util.List;

public record UpdateStaffRequest(
        String fullName,
        String phone,
        String address,
        String designation,
        String department,
        String qualification,
        BigDecimal salaryBase,
        List<String> skills,
        String photoUrl
) {}
