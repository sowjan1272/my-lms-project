package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.auth.*;
import com.company.studentmanagement.entity.*;
import com.company.studentmanagement.enums.AccountStatus;
import com.company.studentmanagement.enums.Role;
import com.company.studentmanagement.enums.StudentStatus;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.*;
import com.company.studentmanagement.security.CustomUserDetails;
import com.company.studentmanagement.security.JwtService;
import com.company.studentmanagement.security.UserDetailsServiceImpl;
import com.company.studentmanagement.service.ActivityLogService;
import com.company.studentmanagement.service.AuthService;
import com.company.studentmanagement.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final StaffSkillRepository staffSkillRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.email());
        User user = userDetails.getUser();

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        String fullName = resolveFullName(user);

        return new LoginResponse(accessToken, refreshToken, user.getId(), user.getEmail(),
                user.getRole().name(), user.getStatus().name(), fullName);
    }

    @Override
    public LoginResponse refresh(RefreshRequest request) {
        String email = jwtService.extractEmail(request.refreshToken());
        if (jwtService.isExpired(request.refreshToken())) {
            throw ApiException.badRequest("Refresh token expired. Please log in again.");
        }
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return new LoginResponse(accessToken, newRefreshToken, user.getId(), user.getEmail(),
                user.getRole().name(), user.getStatus().name(), resolveFullName(user));
    }

    @Override
    @Transactional
    public void registerStudent(RegisterStudentRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw ApiException.badRequest("Passwords do not match.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("An account with this email already exists.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.STUDENT);
        user.setStatus(AccountStatus.ACTIVE);
        user = userRepository.save(user);

        long sequence = studentRepository.count() + 1;
        String code = CodeGenerator.studentCode(sequence);
        while (studentRepository.existsByStudentCode(code)) {
            sequence++;
            code = CodeGenerator.studentCode(sequence);
        }

        Student student = new Student();
        student.setUser(user);
        student.setStudentCode(code);
        student.setFullName(request.fullName());
        student.setPhotoUrl(request.photoUrl());
        student.setDob(request.dob());
        student.setGender(request.gender());
        student.setPhone(request.phone());
        student.setAddress(request.address());
        student.setEmergencyContact(request.emergencyContact());
        student.setQualification(request.qualification());
        student.setCollege(request.college());
        student.setStatus(StudentStatus.PENDING);
        studentRepository.save(student);

        activityLogService.log(user.getId(), "STUDENT_REGISTERED", "STUDENT", student.getId());
    }

    @Override
    @Transactional
    public void registerStaff(RegisterStaffRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw ApiException.badRequest("Passwords do not match.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("An account with this email already exists.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.STAFF);
        user.setStatus(AccountStatus.PENDING); // admin must activate
        user = userRepository.save(user);

        long sequence = staffRepository.count() + 1;
        String code = CodeGenerator.staffCode(sequence);
        while (staffRepository.existsByStaffCode(code)) {
            sequence++;
            code = CodeGenerator.staffCode(sequence);
        }

        Staff staff = new Staff();
        staff.setUser(user);
        staff.setStaffCode(code);
        staff.setFullName(request.fullName());
        staff.setPhotoUrl(request.photoUrl());
        staff.setPhone(request.phone());
        staff.setAddress(request.address());
        staff.setDateOfJoining(request.dateOfJoining());
        staff.setDesignation(request.designation());
        staff.setDepartment(request.department());
        staff.setQualification(request.qualification());
        staff.setSalaryBase(request.salary());
        staff.setActive(false); // admin activates
        staff = staffRepository.save(staff);

        if (request.skills() != null) {
            for (String skill : request.skills()) {
                staffSkillRepository.save(new StaffSkill(staff, skill));
            }
        }

        activityLogService.log(user.getId(), "STAFF_REGISTERED", "STAFF", staff.getId());
    }

    @Override
    @Transactional
    public void createAdmin(CreateAdminRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw ApiException.badRequest("Passwords do not match.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("An account with this email already exists.");
        }

        User admin = new User();
        admin.setEmail(request.email());
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);
        admin.setStatus(AccountStatus.ACTIVE);
        admin = userRepository.save(admin);

        activityLogService.log(admin.getId(), "ADMIN_CREATED", "USER", admin.getId());
    }

    private String resolveFullName(User user) {
        if (user.getRole() == Role.STUDENT) {
            return studentRepository.findByUserId(user.getId()).map(Student::getFullName).orElse(user.getEmail());
        } else if (user.getRole() == Role.STAFF) {
            return staffRepository.findByUserId(user.getId()).map(Staff::getFullName).orElse(user.getEmail());
        }
        return "Administrator";
    }
}
