package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByStaffId(Long staffId);
    List<ProjectMember> findByStudentId(Long studentId);
    List<ProjectMember> findByProjectId(Long projectId);
}
