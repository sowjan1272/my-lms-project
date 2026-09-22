package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.student.*;
import com.company.studentmanagement.entity.Student;

public interface StudentService {
    PageResponse<StudentSummaryDto> listAll(String search, int page, int size);
    PageResponse<StudentSummaryDto> listForMentor(Long staffId, int page, int size);
    StudentDetailDto getById(Long id);
    StudentDetailDto getBySelf(Long userId);
    Student getEntityOrThrow(Long id);
    Student getEntityByUserId(Long userId);
    StudentDetailDto updateSelf(Long userId, UpdateStudentRequest request);
    StudentDetailDto updateStatus(Long studentId, String status);
    StudentDetailDto assignMentor(Long studentId, Long staffId);
}
