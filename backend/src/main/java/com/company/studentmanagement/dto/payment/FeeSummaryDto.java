package com.company.studentmanagement.dto.payment;

import java.math.BigDecimal;

public record FeeSummaryDto(
        BigDecimal totalFee,
        BigDecimal amountPaid,
        BigDecimal remaining,
        String paymentStatus
) {}
