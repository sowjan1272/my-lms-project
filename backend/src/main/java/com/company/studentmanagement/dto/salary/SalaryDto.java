package com.company.studentmanagement.dto.salary;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalaryDto(
        Long id,
        String salaryMonth,
        BigDecimal amount,
        String status,
        LocalDateTime paidAt,
        String method,
        String transactionRef,
        String remarks
) {}
