package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.payment.*;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.PaymentService;
import com.company.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final StudentService studentService;
    private final CurrentUserProvider currentUser;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDto record(@Valid @RequestBody RecordPaymentRequest request) {
        return paymentService.record(currentUser.userId(), request);
    }

    @GetMapping("/student/me")
    @PreAuthorize("hasRole('STUDENT')")
    public List<PaymentDto> myPayments() {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return paymentService.history(student.getId());
    }

    @GetMapping("/student/me/summary")
    @PreAuthorize("hasRole('STUDENT')")
    public FeeSummaryDto myFeeSummary() {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return paymentService.feeSummary(student.getId());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentDto> history(@PathVariable Long studentId) {
        return paymentService.history(studentId);
    }

    @GetMapping("/student/{studentId}/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public FeeSummaryDto feeSummary(@PathVariable Long studentId) {
        return paymentService.feeSummary(studentId);
    }
}
