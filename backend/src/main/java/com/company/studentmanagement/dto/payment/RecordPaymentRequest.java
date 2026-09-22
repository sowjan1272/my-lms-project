package com.company.studentmanagement.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecordPaymentRequest(
        @NotNull Long studentId,
        Long enrollmentId,
        @NotNull @Positive BigDecimal amount,
        @NotNull String method,
        String transactionRef
) {}
