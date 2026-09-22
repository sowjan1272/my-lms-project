package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            Long studentId, LocalDate from, LocalDate to);
    List<Attendance> findByStaffIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            Long staffId, LocalDate from, LocalDate to);
    Optional<Attendance> findByStudentIdAndAttendanceDate(Long studentId, LocalDate date);
    Optional<Attendance> findByStaffIdAndAttendanceDate(Long staffId, LocalDate date);
    long countByStudentIdAndStatus(Long studentId, com.company.studentmanagement.enums.AttendanceStatus status);
    long countByStaffIdAndStatus(Long staffId, com.company.studentmanagement.enums.AttendanceStatus status);
    long countByAttendanceDate(LocalDate date);
    List<Attendance> findByApprovalStatusAndStudent_Mentor_IdOrderByAttendanceDateDesc(
            com.company.studentmanagement.enums.ApprovalStatus approvalStatus, Long mentorStaffId);
    List<Attendance> findByApprovalStatusAndStaffIsNotNullOrderByAttendanceDateDesc(
            com.company.studentmanagement.enums.ApprovalStatus approvalStatus);
}
