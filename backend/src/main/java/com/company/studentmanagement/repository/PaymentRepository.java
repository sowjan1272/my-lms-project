package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudentIdOrderByPaidAtDesc(Long studentId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.student.id = :studentId")
    BigDecimal sumAmountByStudentId(Long studentId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p")
    BigDecimal sumAllAmounts();
}
