package com.company.studentmanagement.dto.salary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateSalaryRequest(
        @NotNull Long staffId,
        @NotBlank String salaryMonth,
        @NotNull BigDecimal amount
) {}
