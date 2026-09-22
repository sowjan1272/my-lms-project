package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.student.*;
import com.company.studentmanagement.entity.Staff;
import com.company.studentmanagement.entity.Student;
import com.company.studentmanagement.enums.AttendanceStatus;
import com.company.studentmanagement.enums.StudentStatus;
import com.company.studentmanagement.enums.TaskStatus;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.*;
import com.company.studentmanagement.service.ActivityLogService;
import com.company.studentmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final AttendanceRepository attendanceRepository;
    private final TaskRepository taskRepository;
    private final ActivityLogService activityLogService;

    @Override
    public PageResponse<StudentSummaryDto> listAll(String search, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String q = search == null ? "" : search;
        var result = studentRepository
                .findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCase(q, q, pageable);
        return PageResponse.from(result.map(this::toSummary));
    }

    @Override
    public PageResponse<StudentSummaryDto> listForMentor(Long staffId, int page, int size) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> ApiException.notFound("Staff not found"));
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = studentRepository.findByMentor(staff, pageable);
        return PageResponse.from(result.map(this::toSummary));
    }

    @Override
    public StudentDetailDto getById(Long id) {
        return toDetail(getEntityOrThrow(id));
    }

    @Override
    public StudentDetailDto getBySelf(Long userId) {
        return toDetail(getEntityByUserId(userId));
    }

    @Override
    public Student getEntityOrThrow(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> ApiException.notFound("Student not found"));
    }

    @Override
    public Student getEntityByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> ApiException.notFound("Student profile not found"));
    }

    @Override
    @Transactional
    public StudentDetailDto updateSelf(Long userId, UpdateStudentRequest request) {
        Student student = getEntityByUserId(userId);
        if (request.fullName() != null) student.setFullName(request.fullName());
        if (request.phone() != null) student.setPhone(request.phone());
        if (request.address() != null) student.setAddress(request.address());
        if (request.emergencyContact() != null) student.setEmergencyContact(request.emergencyContact());
        if (request.qualification() != null) student.setQualification(request.qualification());
        if (request.college() != null) student.setCollege(request.college());
        if (request.photoUrl() != null) student.setPhotoUrl(request.photoUrl());
        studentRepository.save(student);
        return toDetail(student);
    }

    @Override
    @Transactional
    public StudentDetailDto updateStatus(Long studentId, String status) {
        Student student = getEntityOrThrow(studentId);
        StudentStatus newStatus;
        try {
            newStatus = StudentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Invalid status value: " + status);
        }
        student.setStatus(newStatus);
        studentRepository.save(student);
        activityLogService.log(student.getUser().getId(), "STUDENT_STATUS_UPDATED", "STUDENT", student.getId());
        return toDetail(student);
    }

    @Override
    @Transactional
    public StudentDetailDto assignMentor(Long studentId, Long staffId) {
        Student student = getEntityOrThrow(studentId);
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> ApiException.notFound("Staff not found"));
        student.setMentor(staff);
        studentRepository.save(student);
        activityLogService.log(student.getUser().getId(), "MENTOR_ASSIGNED", "STUDENT", student.getId());
        return toDetail(student);
    }

    private StudentSummaryDto toSummary(Student s) {
        long present = attendanceRepository.countByStudentIdAndStatus(s.getId(), AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStudentIdAndStatus(s.getId(), AttendanceStatus.ABSENT);
        double attendancePct = (present + absent) == 0 ? 0.0 : (present * 100.0) / (present + absent);

        long completed = taskRepository.countByStudentIdAndStatus(s.getId(), TaskStatus.COMPLETED);
        long total = s.getId() == null ? 0 : taskRepositoryTotalForStudent(s.getId());
        double taskPct = total == 0 ? 0.0 : (completed * 100.0) / total;

        return new StudentSummaryDto(
                s.getId(), s.getStudentCode(), s.getFullName(), s.getPhotoUrl(),
                s.getUser().getEmail(), s.getPhone(), s.getStatus().name(),
                s.getMentor() != null ? s.getMentor().getFullName() : null,
                Math.round(attendancePct * 10) / 10.0,
                Math.round(taskPct * 10) / 10.0
        );
    }

    private long taskRepositoryTotalForStudent(Long studentId) {
        return taskRepository.findByStudentIdOrderByDueDateAsc(studentId).size();
    }

    private StudentDetailDto toDetail(Student s) {
        return new StudentDetailDto(
                s.getId(), s.getStudentCode(), s.getFullName(), s.getPhotoUrl(),
                s.getUser().getEmail(), s.getDob(), s.getGender(), s.getPhone(), s.getAddress(),
                s.getEmergencyContact(), s.getQualification(), s.getCollege(), s.getStatus().name(),
                s.getMentor() != null ? s.getMentor().getId() : null,
                s.getMentor() != null ? s.getMentor().getFullName() : null,
                s.getCreatedAt()
        );
    }
}
