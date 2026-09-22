package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.dashboard.*;
import com.company.studentmanagement.entity.Enrollment;
import com.company.studentmanagement.enums.*;
import com.company.studentmanagement.repository.*;
import com.company.studentmanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final AttendanceRepository attendanceRepository;
    private final TaskRepository taskRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SalaryPaymentRepository salaryPaymentRepository;

    @Override
    public StudentDashboardDto studentDashboard(Long studentId) {
        long present = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.ABSENT);
        double attendancePct = (present + absent) == 0 ? 0.0 : (present * 100.0) / (present + absent);

        long total = taskRepository.findByStudentIdOrderByDueDateAsc(studentId).size();
        long completed = taskRepository.countByStudentIdAndStatus(studentId, TaskStatus.COMPLETED);
        long pending = taskRepository.countByStudentIdAndStatus(studentId, TaskStatus.PENDING);

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        String courseName = enrollments.isEmpty() ? null : enrollments.get(0).getCourse().getName();

        BigDecimal totalFee = enrollments.stream().map(Enrollment::getTotalFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paid = paymentRepository.sumAmountByStudentId(studentId);
        BigDecimal remaining = totalFee.subtract(paid).max(BigDecimal.ZERO);

        return new StudentDashboardDto(Math.round(attendancePct * 10) / 10.0, total, completed, pending,
                courseName, paid, remaining);
    }

    @Override
    public StaffDashboardDto staffDashboard(Long staffId) {
        long present = attendanceRepository.countByStaffIdAndStatus(staffId, AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStaffIdAndStatus(staffId, AttendanceStatus.ABSENT);
        double attendancePct = (present + absent) == 0 ? 0.0 : (present * 100.0) / (present + absent);

        var staff = staffRepository.findById(staffId).orElseThrow();
        long studentsAssigned = studentRepository.findByMentor(staff).size();

        long active = taskRepository.countByAssignedByIdAndStatus(staffId, TaskStatus.IN_PROGRESS)
                + taskRepository.countByAssignedByIdAndStatus(staffId, TaskStatus.PENDING);
        long completed = taskRepository.countByAssignedByIdAndStatus(staffId, TaskStatus.COMPLETED);
        long pending = taskRepository.countByAssignedByIdAndStatus(staffId, TaskStatus.PENDING);

        long projects = projectMemberRepository.findByStaffId(staffId).size();

        String currentMonth = YearMonth.now().toString();
        String salaryStatus = salaryPaymentRepository.findByStaffIdAndSalaryMonth(staffId, currentMonth)
                .map(sp -> sp.getStatus().name()).orElse("NOT_GENERATED");

        return new StaffDashboardDto(Math.round(attendancePct * 10) / 10.0, studentsAssigned, active,
                completed, pending, projects, salaryStatus);
    }

    @Override
    public AdminDashboardDto adminDashboard() {
        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long completedStudents = studentRepository.countByStatus(StudentStatus.COMPLETED);
        long totalStaff = staffRepository.count();
        long activeStaff = staffRepository.countByActiveTrue();
        long todaysAttendance = attendanceRepository.countByAttendanceDate(LocalDate.now());
        long pendingTasks = taskRepository.countByStatus(TaskStatus.PENDING);
        long completedTasks = taskRepository.countByStatus(TaskStatus.COMPLETED);
        BigDecimal revenue = paymentRepository.sumAllAmounts();
        long salaryPending = salaryPaymentRepository.countByStatus(SalaryStatus.PENDING);

        return new AdminDashboardDto(totalStudents, activeStudents, completedStudents, totalStaff,
                activeStaff, todaysAttendance, pendingTasks, completedTasks, revenue, salaryPending);
    }
}
