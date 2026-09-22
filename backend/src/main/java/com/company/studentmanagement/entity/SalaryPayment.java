package com.company.studentmanagement.entity;

import com.company.studentmanagement.enums.SalaryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_payments")
@Getter
@Setter
@NoArgsConstructor
public class SalaryPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "salary_month", nullable = false, columnDefinition = "CHAR(7)")
    private String salaryMonth; // "2026-08"

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalaryStatus status = SalaryStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(length = 50)
    private String method;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Column(length = 255)
    private String remarks;
}
