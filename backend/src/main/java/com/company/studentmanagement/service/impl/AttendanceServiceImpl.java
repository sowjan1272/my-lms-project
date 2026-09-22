package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.attendance.*;
import com.company.studentmanagement.entity.Attendance;
import com.company.studentmanagement.entity.Staff;
import com.company.studentmanagement.entity.Student;
import com.company.studentmanagement.enums.ApprovalStatus;
import com.company.studentmanagement.enums.AttendanceStatus;
import com.company.studentmanagement.enums.Role;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.AttendanceRepository;
import com.company.studentmanagement.repository.StaffRepository;
import com.company.studentmanagement.repository.StudentRepository;
import com.company.studentmanagement.repository.UserRepository;
import com.company.studentmanagement.service.ActivityLogService;
import com.company.studentmanagement.service.AttendanceService;
import com.company.studentmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public AttendanceDto mark(Long markedByUserId, MarkAttendanceRequest request) {
        if ((request.studentId() == null) == (request.staffId() == null)) {
            throw ApiException.badRequest("Exactly one of studentId or staffId must be provided.");
        }

        Attendance attendance;
        if (request.studentId() != null) {
            Student student = studentRepository.findById(request.studentId())
                    .orElseThrow(() -> ApiException.notFound("Student not found"));
            attendance = attendanceRepository.findByStudentIdAndAttendanceDate(request.studentId(), request.date())
                    .orElse(new Attendance());
            attendance.setStudent(student);
        } else {
            Staff staff = staffRepository.findById(request.staffId())
                    .orElseThrow(() -> ApiException.notFound("Staff not found"));
            attendance = attendanceRepository.findByStaffIdAndAttendanceDate(request.staffId(), request.date())
                    .orElse(new Attendance());
            attendance.setStaff(staff);
        }

        attendance.setAttendanceDate(request.date());
        attendance.setStatus(AttendanceStatus.valueOf(request.status()));
        attendance.setCheckIn(request.checkIn());
        attendance.setCheckOut(request.checkOut());
        attendance.setRemarks(request.remarks());
        attendance.setMarkedByUserId(markedByUserId);
        attendance.setApprovalStatus(ApprovalStatus.APPROVED);
        attendance.setApprovedByUserId(markedByUserId);
        attendance.setApprovedAt(LocalDateTime.now());

        attendance = attendanceRepository.save(attendance);
        return toDto(attendance);
    }

    @Override
    @Transactional
    public AttendanceDto selfMarkStudent(Long userId, SelfMarkAttendanceRequest request) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> ApiException.notFound("Student profile not found"));

        Attendance attendance = attendanceRepository
                .findByStudentIdAndAttendanceDate(student.getId(), request.date())
                .orElse(new Attendance());
        attendance.setStudent(student);
        attendance.setAttendanceDate(request.date());
        attendance.setStatus(AttendanceStatus.valueOf(request.status()));
        attendance.setCheckIn(request.checkIn());
        attendance.setCheckOut(request.checkOut());
        attendance.setRemarks(request.remarks());
        attendance.setMarkedByUserId(userId);
        attendance.setApprovalStatus(ApprovalStatus.PENDING);
        attendance.setApprovedByUserId(null);
        attendance.setApprovedAt(null);

        attendance = attendanceRepository.save(attendance);

        if (student.getMentor() != null && student.getMentor().getUser() != null) {
            notificationService.notify(student.getMentor().getUser(), "Attendance approval needed",
                    student.getFullName() + " marked their own attendance for " + request.date() + ".");
        }
        activityLogService.log(userId, "ATTENDANCE_SELF_MARKED_STUDENT", "ATTENDANCE", attendance.getId());

        return toDto(attendance);
    }

    @Override
    @Transactional
    public AttendanceDto selfMarkStaff(Long userId, SelfMarkAttendanceRequest request) {
        Staff staff = staffRepository.findByUserId(userId)
                .orElseThrow(() -> ApiException.notFound("Staff profile not found"));

        Attendance attendance = attendanceRepository
                .findByStaffIdAndAttendanceDate(staff.getId(), request.date())
                .orElse(new Attendance());
        attendance.setStaff(staff);
        attendance.setAttendanceDate(request.date());
        attendance.setStatus(AttendanceStatus.valueOf(request.status()));
        attendance.setCheckIn(request.checkIn());
        attendance.setCheckOut(request.checkOut());
        attendance.setRemarks(request.remarks());
        attendance.setMarkedByUserId(userId);
        attendance.setApprovalStatus(ApprovalStatus.PENDING);
        attendance.setApprovedByUserId(null);
        attendance.setApprovedAt(null);

        attendance = attendanceRepository.save(attendance);

        for (var admin : userRepository.findByRole(Role.ADMIN)) {
            notificationService.notify(admin, "Attendance approval needed",
                    staff.getFullName() + " marked their own attendance for " + request.date() + ".");
        }
        activityLogService.log(userId, "ATTENDANCE_SELF_MARKED_STAFF", "ATTENDANCE", attendance.getId());

        return toDto(attendance);
    }

    @Override
    public List<AttendanceDto> studentCalendar(Long studentId, LocalDate from, LocalDate to) {
        return attendanceRepository
                .findByStudentIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(studentId, from, to)
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<AttendanceDto> staffCalendar(Long staffId, LocalDate from, LocalDate to) {
        return attendanceRepository
                .findByStaffIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(staffId, from, to)
                .stream().map(this::toDto).toList();
    }

    @Override
    public AttendanceSummaryDto studentSummary(Long studentId) {
        long present = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.ABSENT);
        long leave = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.LEAVE);
        long total = present + absent + leave;
        double pct = total == 0 ? 0.0 : (present * 100.0) / total;
        return new AttendanceSummaryDto(total, present, absent, leave, Math.round(pct * 10) / 10.0);
    }

    @Override
    public AttendanceSummaryDto staffSummary(Long staffId) {
        long present = attendanceRepository.countByStaffIdAndStatus(staffId, AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStaffIdAndStatus(staffId, AttendanceStatus.ABSENT);
        long leave = attendanceRepository.countByStaffIdAndStatus(staffId, AttendanceStatus.LEAVE);
        long total = present + absent + leave;
        double pct = total == 0 ? 0.0 : (present * 100.0) / total;
        return new AttendanceSummaryDto(total, present, absent, leave, Math.round(pct * 10) / 10.0);
    }

    @Override
    public List<PendingAttendanceDto> pendingForMentor(Long mentorStaffId) {
        return attendanceRepository
                .findByApprovalStatusAndStudent_Mentor_IdOrderByAttendanceDateDesc(ApprovalStatus.PENDING, mentorStaffId)
                .stream().map(a -> toPendingDto(a, a.getStudent().getId(), a.getStudent().getFullName())).toList();
    }

    @Override
    public List<PendingAttendanceDto> pendingForAdmin() {
        return attendanceRepository
                .findByApprovalStatusAndStaffIsNotNullOrderByAttendanceDateDesc(ApprovalStatus.PENDING)
                .stream().map(a -> toPendingDto(a, a.getStaff().getId(), a.getStaff().getFullName())).toList();
    }

    @Override
    @Transactional
    public AttendanceDto approve(Long approverUserId, Long attendanceId, String approverRole) {
        Attendance attendance = getAndAuthorize(attendanceId, approverUserId, approverRole);
        attendance.setApprovalStatus(ApprovalStatus.APPROVED);
        attendance.setApprovedByUserId(approverUserId);
        attendance.setApprovedAt(LocalDateTime.now());
        attendance = attendanceRepository.save(attendance);

        notifyOwner(attendance, "Attendance approved",
                "Your attendance for " + attendance.getAttendanceDate() + " was approved.");
        activityLogService.log(approverUserId, "ATTENDANCE_APPROVED", "ATTENDANCE", attendance.getId());
        return toDto(attendance);
    }

    @Override
    @Transactional
    public AttendanceDto reject(Long approverUserId, Long attendanceId, String approverRole) {
        Attendance attendance = getAndAuthorize(attendanceId, approverUserId, approverRole);
        attendance.setApprovalStatus(ApprovalStatus.REJECTED);
        attendance.setApprovedByUserId(approverUserId);
        attendance.setApprovedAt(LocalDateTime.now());
        attendance = attendanceRepository.save(attendance);

        notifyOwner(attendance, "Attendance rejected",
                "Your attendance for " + attendance.getAttendanceDate() + " was rejected. Please contact your mentor/admin.");
        activityLogService.log(approverUserId, "ATTENDANCE_REJECTED", "ATTENDANCE", attendance.getId());
        return toDto(attendance);
    }

    private Attendance getAndAuthorize(Long attendanceId, Long approverUserId, String approverRole) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> ApiException.notFound("Attendance record not found"));
        if (attendance.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw ApiException.badRequest("This attendance record has already been reviewed.");
        }
        if ("STAFF".equals(approverRole)) {
            if (attendance.getStudent() == null) {
                throw ApiException.forbidden("Staff can only approve student attendance.");
            }
            Staff mentor = staffRepository.findByUserId(approverUserId)
                    .orElseThrow(() -> ApiException.notFound("Staff profile not found"));
            Student student = attendance.getStudent();
            if (student.getMentor() == null || !student.getMentor().getId().equals(mentor.getId())) {
                throw ApiException.forbidden("You can only approve attendance for your own students.");
            }
        } else if (!"ADMIN".equals(approverRole)) {
            throw ApiException.forbidden("Only staff or admin can approve attendance.");
        }
        return attendance;
    }

    private void notifyOwner(Attendance attendance, String title, String body) {
        if (attendance.getStudent() != null && attendance.getStudent().getUser() != null) {
            notificationService.notify(attendance.getStudent().getUser(), title, body);
        } else if (attendance.getStaff() != null && attendance.getStaff().getUser() != null) {
            notificationService.notify(attendance.getStaff().getUser(), title, body);
        }
    }

    private PendingAttendanceDto toPendingDto(Attendance a, Long personId, String personName) {
        return new PendingAttendanceDto(a.getId(), personId, personName, a.getAttendanceDate(),
                a.getStatus().name(), a.getCheckIn(), a.getCheckOut(), a.getRemarks(), a.getCreatedAt());
    }

    private AttendanceDto toDto(Attendance a) {
        return new AttendanceDto(a.getId(), a.getAttendanceDate(), a.getStatus().name(),
                a.getApprovalStatus().name(), a.getCheckIn(), a.getCheckOut(), a.getRemarks());
    }
}
