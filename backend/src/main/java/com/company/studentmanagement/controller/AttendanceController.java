package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.attendance.*;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.AttendanceService;
import com.company.studentmanagement.service.StaffService;
import com.company.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final CurrentUserProvider currentUser;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public AttendanceDto mark(@Valid @RequestBody MarkAttendanceRequest request) {
        if ("STAFF".equals(currentUser.role()) && request.studentId() != null) {
            var staff = staffService.getEntityByUserId(currentUser.userId());
            var student = studentService.getEntityOrThrow(request.studentId());
            if (student.getMentor() == null || !student.getMentor().getId().equals(staff.getId())) {
                throw ApiException.forbidden("You can only mark attendance for your own students.");
            }
        }
        if ("STAFF".equals(currentUser.role()) && request.staffId() != null) {
            throw ApiException.forbidden("Staff cannot mark their own attendance this way. Use self-mark instead.");
        }
        return attendanceService.mark(currentUser.userId(), request);
    }

    @PostMapping("/self/student")
    @PreAuthorize("hasRole('STUDENT')")
    public AttendanceDto selfMarkStudent(@Valid @RequestBody SelfMarkAttendanceRequest request) {
        return attendanceService.selfMarkStudent(currentUser.userId(), request);
    }

    @PostMapping("/self/staff")
    @PreAuthorize("hasRole('STAFF')")
    public AttendanceDto selfMarkStaff(@Valid @RequestBody SelfMarkAttendanceRequest request) {
        return attendanceService.selfMarkStaff(currentUser.userId(), request);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public List<PendingAttendanceDto> pending() {
        if ("ADMIN".equals(currentUser.role())) {
            return attendanceService.pendingForAdmin();
        }
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return attendanceService.pendingForMentor(staff.getId());
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public AttendanceDto approve(@PathVariable Long id) {
        return attendanceService.approve(currentUser.userId(), id, currentUser.role());
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public AttendanceDto reject(@PathVariable Long id) {
        return attendanceService.reject(currentUser.userId(), id, currentUser.role());
    }

    @GetMapping("/student/me")
    @PreAuthorize("hasRole('STUDENT')")
    public List<AttendanceDto> myStudentCalendar(
            @RequestParam LocalDate from, @RequestParam LocalDate to) {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return attendanceService.studentCalendar(student.getId(), from, to);
    }

    @GetMapping("/student/me/summary")
    @PreAuthorize("hasRole('STUDENT')")
    public AttendanceSummaryDto myStudentSummary() {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return attendanceService.studentSummary(student.getId());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public List<AttendanceDto> studentCalendar(
            @PathVariable Long studentId, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return attendanceService.studentCalendar(studentId, from, to);
    }

    @GetMapping("/staff/me")
    @PreAuthorize("hasRole('STAFF')")
    public List<AttendanceDto> myStaffCalendar(
            @RequestParam LocalDate from, @RequestParam LocalDate to) {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return attendanceService.staffCalendar(staff.getId(), from, to);
    }

    @GetMapping("/staff/me/summary")
    @PreAuthorize("hasRole('STAFF')")
    public AttendanceSummaryDto myStaffSummary() {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return attendanceService.staffSummary(staff.getId());
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AttendanceDto> staffCalendar(
            @PathVariable Long staffId, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return attendanceService.staffCalendar(staffId, from, to);
    }
}
