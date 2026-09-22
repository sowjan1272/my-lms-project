package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.SalaryPayment;
import com.company.studentmanagement.enums.SalaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryPaymentRepository extends JpaRepository<SalaryPayment, Long> {
    List<SalaryPayment> findByStaffIdOrderBySalaryMonthDesc(Long staffId);
    Optional<SalaryPayment> findByStaffIdAndSalaryMonth(Long staffId, String salaryMonth);
    long countByStatus(SalaryStatus status);
}
