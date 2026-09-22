package com.company.studentmanagement.config;

import com.company.studentmanagement.entity.*;
import com.company.studentmanagement.enums.*;
import com.company.studentmanagement.repository.*;
import com.company.studentmanagement.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Seeds demo data for local development only.
 * Activate with: --spring.profiles.active=dev
 * NEVER enable this profile in production.
 *
 * Demo login credentials (all passwords: Password@123):
 *   Admin:   admin@company.com
 *   Staff:   arun.staff@company.com, priya.staff@company.com
 *   Student: rahul.student@company.com, sneha.student@company.com
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final StaffSkillRepository staffSkillRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TaskRepository taskRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final SalaryPaymentRepository salaryPaymentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // already seeded
        }

        String pw = passwordEncoder.encode("Password@123");

        // --- Admin ---
        User adminUser = new User();
        adminUser.setEmail("admin@company.com");
        adminUser.setPasswordHash(pw);
        adminUser.setRole(Role.ADMIN);
        adminUser.setStatus(AccountStatus.ACTIVE);
        userRepository.save(adminUser);

        // --- Staff ---
        Staff arun = createStaff("arun.staff@company.com", pw, "STAFF-2026-0001", "Arun Kumar",
                "Senior Software Engineer", "Engineering", new BigDecimal("45000"),
                List.of("Java", "Spring Boot", "React"));
        Staff priya = createStaff("priya.staff@company.com", pw, "STAFF-2026-0002", "Priya Sharma",
                "Technical Mentor", "Engineering", new BigDecimal("40000"),
                List.of("Python", "Data Science", "SQL"));

        // --- Courses ---
        Course fullStack = new Course();
        fullStack.setName("Full Stack Development");
        fullStack.setDescription("End-to-end web development with React and Spring Boot.");
        fullStack.setType(CourseType.INTERNSHIP);
        fullStack.setDurationMonths(6);
        fullStack.setFee(new BigDecimal("30000"));
        fullStack.setMentor(arun);
        fullStack.getModules().add(new CourseModule(fullStack, "HTML/CSS/JS Fundamentals", 0));
        fullStack.getModules().add(new CourseModule(fullStack, "React & TypeScript", 1));
        fullStack.getModules().add(new CourseModule(fullStack, "Spring Boot & REST APIs", 2));
        fullStack.getModules().add(new CourseModule(fullStack, "Capstone Project", 3));
        courseRepository.save(fullStack);

        Course dataScience = new Course();
        dataScience.setName("Data Science with Python");
        dataScience.setDescription("Python, statistics, and machine learning fundamentals.");
        dataScience.setType(CourseType.COURSE);
        dataScience.setDurationMonths(4);
        dataScience.setFee(new BigDecimal("22000"));
        dataScience.setMentor(priya);
        dataScience.getModules().add(new CourseModule(dataScience, "Python Basics", 0));
        dataScience.getModules().add(new CourseModule(dataScience, "Pandas & NumPy", 1));
        dataScience.getModules().add(new CourseModule(dataScience, "ML Fundamentals", 2));
        courseRepository.save(dataScience);

        // --- Students ---
        Student rahul = createStudent("rahul.student@company.com", pw, "STU-2026-0001", "Rahul Verma",
                arun, LocalDate.of(2003, 4, 12));
        Student sneha = createStudent("sneha.student@company.com", pw, "STU-2026-0002", "Sneha Iyer",
                arun, LocalDate.of(2003, 8, 21));
        Student vikram = createStudent("vikram.student@company.com", pw, "STU-2026-0003", "Vikram Singh",
                priya, LocalDate.of(2002, 11, 3));

        // --- Enrollments ---
        enroll(rahul, fullStack, new BigDecimal("30000"), 40);
        enroll(sneha, fullStack, new BigDecimal("30000"), 25);
        enroll(vikram, dataScience, new BigDecimal("22000"), 60);

        // --- Payments ---
        recordPayment(rahul, new BigDecimal("20000"), "UPI", adminUser.getId());
        recordPayment(sneha, new BigDecimal("15000"), "Bank Transfer", adminUser.getId());
        recordPayment(vikram, new BigDecimal("22000"), "Card", adminUser.getId());

        // --- Attendance (last 5 days) ---
        for (int i = 0; i < 5; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            markAttendance(rahul, null, date, i == 2 ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT, adminUser.getId());
            markAttendance(sneha, null, date, AttendanceStatus.PRESENT, adminUser.getId());
            markAttendance(null, arun, date, AttendanceStatus.PRESENT, adminUser.getId());
        }

        // --- Tasks ---
        Task t1 = createTask("Build Login Page", "Implement the login UI with validation.",
                arun, rahul, TaskPriority.HIGH, LocalDate.now().plusDays(3), TaskStatus.PENDING);
        Task t2 = createTask("Design DB Schema", "Design the ER diagram for the app.",
                arun, sneha, TaskPriority.HIGH, LocalDate.now().plusDays(5), TaskStatus.IN_PROGRESS);
        createTask("EDA on sample dataset", "Perform exploratory data analysis.",
                priya, vikram, TaskPriority.HIGH, LocalDate.now().plusDays(7), TaskStatus.PENDING);

        // --- Salary ---
        createSalary(arun, java.time.YearMonth.now().toString(), new BigDecimal("45000"), SalaryStatus.PAID);
        createSalary(priya, java.time.YearMonth.now().toString(), new BigDecimal("40000"), SalaryStatus.PENDING);

        // --- Project ---
        Project project = new Project();
        project.setName("Internal Attendance Tracker");
        project.setDescription("A small internal tool to track intern attendance.");
        project.setTechnologies("React, Spring Boot, MySQL");
        project.setStartDate(LocalDate.now().minusMonths(1));
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setProgressPct(55);
        projectRepository.save(project);

        ProjectMember pm1 = new ProjectMember();
        pm1.setProject(project);
        pm1.setStaff(arun);
        pm1.setRoleLabel("Tech Lead");
        projectMemberRepository.save(pm1);

        ProjectMember pm2 = new ProjectMember();
        pm2.setProject(project);
        pm2.setStudent(rahul);
        pm2.setRoleLabel("Frontend Developer");
        projectMemberRepository.save(pm2);
    }

    private Staff createStaff(String email, String pw, String code, String name, String designation,
                               String department, BigDecimal salary, List<String> skills) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(pw);
        user.setRole(Role.STAFF);
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        Staff staff = new Staff();
        staff.setUser(user);
        staff.setStaffCode(code);
        staff.setFullName(name);
        staff.setPhone("9876543210");
        staff.setDateOfJoining(LocalDate.now().minusYears(1));
        staff.setDesignation(designation);
        staff.setDepartment(department);
        staff.setSalaryBase(salary);
        staff.setActive(true);
        staff = staffRepository.save(staff);

        for (String skill : skills) {
            staffSkillRepository.save(new StaffSkill(staff, skill));
        }
        return staff;
    }

    private Student createStudent(String email, String pw, String code, String name, Staff mentor, LocalDate dob) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(pw);
        user.setRole(Role.STUDENT);
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setStudentCode(code);
        student.setFullName(name);
        student.setDob(dob);
        student.setGender("Not specified");
        student.setPhone("9876500000");
        student.setStatus(StudentStatus.ACTIVE);
        student.setMentor(mentor);
        return studentRepository.save(student);
    }

    private void enroll(Student student, Course course, BigDecimal totalFee, int progressPct) {
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        e.setStartDate(LocalDate.now().minusMonths(2));
        e.setEndDate(LocalDate.now().plusMonths(4));
        e.setTotalFee(totalFee);
        e.setProgressPct(progressPct);
        enrollmentRepository.save(e);
    }

    private void recordPayment(Student student, BigDecimal amount, String method, Long recordedBy) {
        Payment p = new Payment();
        p.setStudent(student);
        p.setAmount(amount);
        p.setMethod(method);
        p.setRecordedByUserId(recordedBy);
        paymentRepository.save(p);
    }

    private void markAttendance(Student student, Staff staff, LocalDate date, AttendanceStatus status, Long markedBy) {
        Attendance a = new Attendance();
        a.setStudent(student);
        a.setStaff(staff);
        a.setAttendanceDate(date);
        a.setStatus(status);
        a.setMarkedByUserId(markedBy);
        attendanceRepository.save(a);
    }

    private Task createTask(String title, String desc, Staff assignedBy, Student student,
                             TaskPriority priority, LocalDate dueDate, TaskStatus status) {
        Task t = new Task();
        t.setTitle(title);
        t.setDescription(desc);
        t.setAssignedBy(assignedBy);
        t.setStudent(student);
        t.setPriority(priority);
        t.setDueDate(dueDate);
        t.setStatus(status);
        return taskRepository.save(t);
    }

    private void createSalary(Staff staff, String month, BigDecimal amount, SalaryStatus status) {
        SalaryPayment sp = new SalaryPayment();
        sp.setStaff(staff);
        sp.setSalaryMonth(month);
        sp.setAmount(amount);
        sp.setStatus(status);
        if (status == SalaryStatus.PAID) {
            sp.setPaidAt(java.time.LocalDateTime.now());
            sp.setMethod("Bank Transfer");
        }
        salaryPaymentRepository.save(sp);
    }
}
