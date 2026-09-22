package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.enrollment.EnrollStudentRequest;
import com.company.studentmanagement.dto.enrollment.EnrollmentDto;

import java.util.List;

public interface EnrollmentService {
    EnrollmentDto enroll(Long actingUserId, EnrollStudentRequest request);
    List<EnrollmentDto> listByStudent(Long studentId);
}
