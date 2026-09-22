package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.dashboard.*;

public interface DashboardService {
    StudentDashboardDto studentDashboard(Long studentId);
    StaffDashboardDto staffDashboard(Long staffId);
    AdminDashboardDto adminDashboard();
}
