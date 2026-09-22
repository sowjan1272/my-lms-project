package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.payment.*;

import java.util.List;

public interface PaymentService {
    PaymentDto record(Long recordedByUserId, RecordPaymentRequest request);
    List<PaymentDto> history(Long studentId);
    FeeSummaryDto feeSummary(Long studentId);
}
