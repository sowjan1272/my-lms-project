package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.Course;
import com.company.studentmanagement.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByType(CourseType type);
    List<Course> findByActiveTrue();
    List<Course> findByMentorId(Long mentorId);
}
