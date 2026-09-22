package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.salary.*;
import com.company.studentmanagement.entity.SalaryPayment;
import com.company.studentmanagement.entity.Staff;
import com.company.studentmanagement.enums.SalaryStatus;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.SalaryPaymentRepository;
import com.company.studentmanagement.repository.StaffRepository;
import com.company.studentmanagement.service.ActivityLogService;
import com.company.studentmanagement.service.NotificationService;
import com.company.studentmanagement.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryPaymentRepository salaryPaymentRepository;
    private final StaffRepository staffRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public SalaryDto create(CreateSalaryRequest request) {
        Staff staff = staffRepository.findById(request.staffId())
                .orElseThrow(() -> ApiException.notFound("Staff not found"));

        if (salaryPaymentRepository.findByStaffIdAndSalaryMonth(staff.getId(), request.salaryMonth()).isPresent()) {
            throw ApiException.conflict("A salary record already exists for this staff and month.");
        }

        SalaryPayment sp = new SalaryPayment();
        sp.setStaff(staff);
        sp.setSalaryMonth(request.salaryMonth());
        sp.setAmount(request.amount());
        sp.setStatus(SalaryStatus.PENDING);
        sp = salaryPaymentRepository.save(sp);

        return toDto(sp);
    }

    @Override
    public List<SalaryDto> history(Long staffId) {
        return salaryPaymentRepository.findByStaffIdOrderBySalaryMonthDesc(staffId).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public SalaryDto updateStatus(Long salaryId, Long staffIdForOwnershipCheck, UpdateSalaryStatusRequest request, boolean isAdmin) {
        SalaryPayment sp = salaryPaymentRepository.findById(salaryId)
                .orElseThrow(() -> ApiException.notFound("Salary record not found"));

        if (!isAdmin && (staffIdForOwnershipCheck == null || !sp.getStaff().getId().equals(staffIdForOwnershipCheck))) {
            throw ApiException.forbidden("You cannot modify another staff member's salary.");
        }
        if (!isAdmin) {
            throw ApiException.forbidden("Only admin can update salary status.");
        }

        SalaryStatus newStatus = SalaryStatus.valueOf(request.status());
        sp.setStatus(newStatus);
        if (newStatus == SalaryStatus.PAID) {
            sp.setPaidAt(LocalDateTime.now());
        }
        sp.setMethod(request.method());
        sp.setTransactionRef(request.transactionRef());
        sp.setRemarks(request.remarks());
        salaryPaymentRepository.save(sp);

        if (newStatus == SalaryStatus.PAID) {
            notificationService.notify(sp.getStaff().getUser(), "Salary paid",
                    "Your salary for " + sp.getSalaryMonth() + " has been marked as paid.");
        }
        activityLogService.log(sp.getStaff().getUser().getId(), "SALARY_STATUS_UPDATED", "SALARY_PAYMENT", sp.getId());

        return toDto(sp);
    }

    private SalaryDto toDto(SalaryPayment sp) {
        return new SalaryDto(sp.getId(), sp.getSalaryMonth(), sp.getAmount(), sp.getStatus().name(),
                sp.getPaidAt(), sp.getMethod(), sp.getTransactionRef(), sp.getRemarks());
    }
}
