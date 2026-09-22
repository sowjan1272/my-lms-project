package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.auth.*;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refresh(RefreshRequest request);
    void registerStudent(RegisterStudentRequest request);
    void registerStaff(RegisterStaffRequest request);
    void createAdmin(CreateAdminRequest request);
}
