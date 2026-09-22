package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUserId(Long userId);
    boolean existsByStaffCode(String code);
    Page<Staff> findByFullNameContainingIgnoreCaseOrStaffCodeContainingIgnoreCase(
            String name, String code, Pageable pageable);
    long countByActiveTrue();
}
