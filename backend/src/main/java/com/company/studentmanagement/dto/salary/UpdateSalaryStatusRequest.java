package com.company.studentmanagement.dto.salary;

import jakarta.validation.constraints.NotBlank;

public record UpdateSalaryStatusRequest(
        @NotBlank String status, // PENDING PROCESSING PAID FAILED
        String method,
        String transactionRef,
        String remarks
) {}
