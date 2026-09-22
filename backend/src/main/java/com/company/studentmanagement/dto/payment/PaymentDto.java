package com.company.studentmanagement.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDto(
        Long id,
        BigDecimal amount,
        String method,
        String transactionRef,
        LocalDateTime paidAt
) {}
