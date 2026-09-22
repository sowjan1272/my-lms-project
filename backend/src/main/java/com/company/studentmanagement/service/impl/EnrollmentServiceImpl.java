package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.enrollment.EnrollStudentRequest;
import com.company.studentmanagement.dto.enrollment.EnrollmentDto;
import com.company.studentmanagement.entity.Course;
import com.company.studentmanagement.entity.Enrollment;
import com.company.studentmanagement.entity.Student;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.CourseRepository;
import com.company.studentmanagement.repository.EnrollmentRepository;
import com.company.studentmanagement.repository.StudentRepository;
import com.company.studentmanagement.service.ActivityLogService;
import com.company.studentmanagement.service.EnrollmentService;
import com.company.studentmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public EnrollmentDto enroll(Long actingUserId, EnrollStudentRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> ApiException.notFound("Student not found"));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> ApiException.notFound("Course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw ApiException.conflict("Student is already enrolled in this course.");
        }

        LocalDate startDate = request.startDate() != null ? request.startDate() : LocalDate.now();

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStartDate(startDate);
        enrollment.setEndDate(startDate.plusMonths(course.getDurationMonths()));
        enrollment.setProgressPct(0);
        enrollment.setTotalFee(request.totalFee() != null ? request.totalFee() : course.getFee());

        enrollment = enrollmentRepository.save(enrollment);

        notificationService.notify(student.getUser(), "Enrolled in a new course",
                "You have been enrolled in " + course.getName() + ".");
        activityLogService.log(actingUserId, "STUDENT_ENROLLED", "ENROLLMENT", enrollment.getId());

        return toDto(enrollment);
    }

    @Override
    public List<EnrollmentDto> listByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream().map(this::toDto).toList();
    }

    private EnrollmentDto toDto(Enrollment e) {
        return new EnrollmentDto(
                e.getId(),
                e.getStudent().getId(),
                e.getStudent().getFullName(),
                e.getCourse().getId(),
                e.getCourse().getName(),
                e.getCourse().getType().name(),
                e.getStartDate(),
                e.getEndDate(),
                e.getProgressPct(),
                e.getTotalFee(),
                e.getCreatedAt()
        );
    }
}
