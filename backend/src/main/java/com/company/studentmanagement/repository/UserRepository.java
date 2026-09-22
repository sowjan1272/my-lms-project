package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    boolean existsByEmail(String email);
    java.util.List<User> findByRole(com.company.studentmanagement.enums.Role role);
}
