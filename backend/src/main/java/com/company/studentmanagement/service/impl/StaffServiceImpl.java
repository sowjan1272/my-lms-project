package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.staff.*;
import com.company.studentmanagement.entity.Staff;
import com.company.studentmanagement.entity.StaffSkill;
import com.company.studentmanagement.enums.AccountStatus;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.StaffRepository;
import com.company.studentmanagement.repository.StaffSkillRepository;
import com.company.studentmanagement.repository.UserRepository;
import com.company.studentmanagement.service.ActivityLogService;
import com.company.studentmanagement.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffSkillRepository staffSkillRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    @Override
    public PageResponse<StaffSummaryDto> listAll(String search, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String q = search == null ? "" : search;
        var result = staffRepository.findByFullNameContainingIgnoreCaseOrStaffCodeContainingIgnoreCase(q, q, pageable);
        return PageResponse.from(result.map(this::toSummary));
    }

    @Override
    public StaffDetailDto getById(Long id) {
        return toDetail(getEntityOrThrow(id));
    }

    @Override
    public StaffDetailDto getBySelf(Long userId) {
        return toDetail(getEntityByUserId(userId));
    }

    @Override
    public Staff getEntityOrThrow(Long id) {
        return staffRepository.findById(id).orElseThrow(() -> ApiException.notFound("Staff not found"));
    }

    @Override
    public Staff getEntityByUserId(Long userId) {
        return staffRepository.findByUserId(userId).orElseThrow(() -> ApiException.notFound("Staff profile not found"));
    }

    @Override
    @Transactional
    public StaffDetailDto updateSelf(Long userId, UpdateStaffRequest request) {
        Staff staff = getEntityByUserId(userId);
        if (request.fullName() != null) staff.setFullName(request.fullName());
        if (request.phone() != null) staff.setPhone(request.phone());
        if (request.address() != null) staff.setAddress(request.address());
        if (request.designation() != null) staff.setDesignation(request.designation());
        if (request.department() != null) staff.setDepartment(request.department());
        if (request.qualification() != null) staff.setQualification(request.qualification());
        if (request.salaryBase() != null) staff.setSalaryBase(request.salaryBase());
        if (request.photoUrl() != null) staff.setPhotoUrl(request.photoUrl());
        if (request.skills() != null) {
            staff.getSkills().clear();
            for (String skill : request.skills()) {
                staff.getSkills().add(new StaffSkill(staff, skill));
            }
        }
        staffRepository.save(staff);
        return toDetail(staff);
    }

    @Override
    @Transactional
    public StaffDetailDto setActive(Long staffId, boolean active) {
        Staff staff = getEntityOrThrow(staffId);
        staff.setActive(active);
        staffRepository.save(staff);

        var user = staff.getUser();
        user.setStatus(active ? AccountStatus.ACTIVE : AccountStatus.INACTIVE);
        userRepository.save(user);

        activityLogService.log(user.getId(), active ? "STAFF_ACTIVATED" : "STAFF_DEACTIVATED", "STAFF", staff.getId());
        return toDetail(staff);
    }

    private StaffSummaryDto toSummary(Staff s) {
        List<String> skills = s.getSkills().stream().map(StaffSkill::getSkill).toList();
        return new StaffSummaryDto(s.getId(), s.getStaffCode(), s.getFullName(), s.getPhotoUrl(),
                s.getUser().getEmail(), s.getDepartment(), s.getDesignation(), s.isActive(),
                s.getSalaryBase(), skills);
    }

    private StaffDetailDto toDetail(Staff s) {
        List<String> skills = s.getSkills().stream().map(StaffSkill::getSkill).toList();
        return new StaffDetailDto(s.getId(), s.getStaffCode(), s.getFullName(), s.getPhotoUrl(),
                s.getUser().getEmail(), s.getPhone(), s.getAddress(), s.getDateOfJoining(),
                s.getDesignation(), s.getDepartment(), s.getQualification(), s.getSalaryBase(),
                s.isActive(), skills);
    }
}
