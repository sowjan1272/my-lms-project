package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.staff.*;
import com.company.studentmanagement.entity.Staff;

public interface StaffService {
    PageResponse<StaffSummaryDto> listAll(String search, int page, int size);
    StaffDetailDto getById(Long id);
    StaffDetailDto getBySelf(Long userId);
    Staff getEntityOrThrow(Long id);
    Staff getEntityByUserId(Long userId);
    StaffDetailDto updateSelf(Long userId, UpdateStaffRequest request);
    StaffDetailDto setActive(Long staffId, boolean active);
}
