package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.Staff;
import com.company.studentmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    boolean existsByStudentCode(String code);
    long countByStatus(com.company.studentmanagement.enums.StudentStatus status);
    Page<Student> findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCase(
            String name, String code, Pageable pageable);
    List<Student> findByMentor(Staff mentor);
    Page<Student> findByMentor(Staff mentor, Pageable pageable);
}
