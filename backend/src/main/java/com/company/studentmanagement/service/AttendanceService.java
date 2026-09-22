package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.attendance.*;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceDto mark(Long markedByUserId, MarkAttendanceRequest request);
    AttendanceDto selfMarkStudent(Long userId, SelfMarkAttendanceRequest request);
    AttendanceDto selfMarkStaff(Long userId, SelfMarkAttendanceRequest request);
    List<AttendanceDto> studentCalendar(Long studentId, LocalDate from, LocalDate to);
    List<AttendanceDto> staffCalendar(Long staffId, LocalDate from, LocalDate to);
    AttendanceSummaryDto studentSummary(Long studentId);
    AttendanceSummaryDto staffSummary(Long staffId);
    List<PendingAttendanceDto> pendingForMentor(Long mentorStaffId);
    List<PendingAttendanceDto> pendingForAdmin();
    AttendanceDto approve(Long approverUserId, Long attendanceId, String approverRole);
    AttendanceDto reject(Long approverUserId, Long attendanceId, String approverRole);
}
