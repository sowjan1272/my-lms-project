package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.salary.*;

import java.util.List;

public interface SalaryService {
    SalaryDto create(CreateSalaryRequest request);
    List<SalaryDto> history(Long staffId);
    SalaryDto updateStatus(Long salaryId, Long staffIdForOwnershipCheck, UpdateSalaryStatusRequest request, boolean isAdmin);
}
