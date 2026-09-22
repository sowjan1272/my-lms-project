package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.payment.*;
import com.company.studentmanagement.entity.Enrollment;
import com.company.studentmanagement.entity.Payment;
import com.company.studentmanagement.entity.Student;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.EnrollmentRepository;
import com.company.studentmanagement.repository.PaymentRepository;
import com.company.studentmanagement.repository.StudentRepository;
import com.company.studentmanagement.service.ActivityLogService;
import com.company.studentmanagement.service.NotificationService;
import com.company.studentmanagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public PaymentDto record(Long recordedByUserId, RecordPaymentRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> ApiException.notFound("Student not found"));

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setAmount(request.amount());
        payment.setMethod(request.method());
        payment.setTransactionRef(request.transactionRef());
        payment.setRecordedByUserId(recordedByUserId);

        if (request.enrollmentId() != null) {
            Enrollment enrollment = enrollmentRepository.findById(request.enrollmentId())
                    .orElseThrow(() -> ApiException.notFound("Enrollment not found"));
            payment.setEnrollment(enrollment);
        }

        payment = paymentRepository.save(payment);

        notificationService.notify(student.getUser(), "Payment recorded",
                "Payment of " + request.amount() + " recorded against your account.");
        activityLogService.log(recordedByUserId, "PAYMENT_RECORDED", "PAYMENT", payment.getId());

        return toDto(payment);
    }

    @Override
    public List<PaymentDto> history(Long studentId) {
        return paymentRepository.findByStudentIdOrderByPaidAtDesc(studentId).stream().map(this::toDto).toList();
    }

    @Override
    public FeeSummaryDto feeSummary(Long studentId) {
        BigDecimal totalFee = enrollmentRepository.findByStudentId(studentId).stream()
                .map(Enrollment::getTotalFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paid = paymentRepository.sumAmountByStudentId(studentId);
        BigDecimal remaining = totalFee.subtract(paid).max(BigDecimal.ZERO);

        String status;
        if (totalFee.compareTo(BigDecimal.ZERO) == 0) {
            status = "PENDING";
        } else if (paid.compareTo(totalFee) >= 0) {
            status = "PAID";
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            status = "PARTIALLY_PAID";
        } else {
            status = "PENDING";
        }

        return new FeeSummaryDto(totalFee, paid, remaining, status);
    }

    private PaymentDto toDto(Payment p) {
        return new PaymentDto(p.getId(), p.getAmount(), p.getMethod(), p.getTransactionRef(), p.getPaidAt());
    }
}
