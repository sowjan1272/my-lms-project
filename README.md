# Student, Staff & Admin Management System

A full-stack management platform for a training/internship company: three role-based
dashboards (Student, Staff, Admin) covering authentication, attendance, tasks,
courses/internships, fees, salaries, and projects.

**Stack:** React + TypeScript + Vite + Tailwind (frontend) · Java 21 + Spring Boot +
Spring Security + Spring Data JPA (backend) · MySQL 8 + Flyway (database) · JWT + BCrypt (auth).

---

## What's fully built

- **Auth & roles**: register (student/staff), login, JWT access + refresh tokens, BCrypt
  hashing, role-based route protection on both frontend and backend, staff accounts require
  admin activation before they can log in as ACTIVE.
- **Database**: all 17 tables from the schema doc, wired with Flyway migrations (`V1`–`V17`).
- **Students**: profile, admin directory with search, staff "my students" view, mentor
  assignment, status management (Active/Completed/Suspended/Dropped/Pending).
- **Staff**: profile, admin directory, activate/deactivate, skills.
- **Courses/Internships**: CRUD, modules, mentor assignment, enrollment counts.
- **Attendance**: mark (staff/admin), student/staff calendar views, present/absent/leave/holiday,
  attendance percentage summaries. Ownership-checked — staff can only mark their own mentees.
- **Tasks**: staff creates/assigns tasks (single or multiple students), student submits
  (text/file/GitHub/demo link), staff reviews (approve/needs revision + feedback), automatic
  daily job flips overdue tasks.
- **Payments**: record payments, per-student fee summary (paid/remaining/status), computed
  from an append-only payment ledger (never overwritten).
- **Salaries**: monthly salary records, admin marks pending/processing/paid/failed.
- **Projects**: company-wide projects with staff/student membership.
- **Notifications**: task/payment/salary/attendance events generate in-app notifications with
  unread counts.
- **Dashboards**: role-specific stat cards backed by real aggregation queries (attendance %,
  task counts, revenue, salary pending, etc).
- **Activity log**: append-only audit trail on key actions (registration, task/payment/salary
  events).
- **Security**: method-level `@PreAuthorize` per role + row-level ownership checks in
  controllers/services (e.g. a staff member can't view another staff member's mentees).

## What's partial or stubbed (be aware before treating this as finished)

- **File uploads**: DTOs accept a `fileUrl`/`photoUrl` string field, but there's no actual
  multipart upload endpoint/storage wired yet — you'd POST a URL from wherever you host files.
- **CSV/PDF export & the full Reports UI**: the Reports page and admin Activity Log page are
  light placeholders — the underlying data/repositories exist, but the export and rich
  filtering UI described in the spec isn't built.
- **Real-time notifications**: notifications are stored and fetchable, but there's no
  WebSocket/polling push — the frontend fetches on demand rather than live-updating.
- **Global search**: no dedicated cross-entity search endpoint; each list page has its own
  search (students, staff) as specified, but there's no single admin-wide search bar.
- **Document management** (`documents` table/entity) has a repository but no controller/UI yet.

---

## Prerequisites

- Java 21, Maven 3.9+
- Node.js 18+, npm
- MySQL 8 running locally (or via Docker)

## 1. Database

```sql
CREATE DATABASE student_management;
```

## 2. Backend

```bash
cd backend
cp .env.example .env    # then edit DB_PASSWORD and JWT_SECRET
```

Export the variables (or use your IDE's run config / a tool like `direnv`) and run:

```bash
export DB_HOST=localhost DB_PORT=3306 DB_NAME=student_management \
       DB_USERNAME=root DB_PASSWORD=your_password \
       JWT_SECRET=$(openssl rand -base64 48) \
       CORS_ORIGIN=http://localhost:5173

# Run with demo data seeded automatically (recommended for first run):
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or without seed data:
mvn spring-boot:run
```

Flyway runs the migrations automatically on startup. The API listens on `http://localhost:8080`.

### Demo login credentials (only when run with the `dev` profile)

| Role | Email | Password |
|---|---|---|
| Admin | admin@company.com | Password@123 |
| Staff | arun.staff@company.com | Password@123 |
| Staff | priya.staff@company.com | Password@123 |
| Student | rahul.student@company.com | Password@123 |
| Student | sneha.student@company.com | Password@123 |

## 3. Frontend

```bash
cd frontend
cp .env.example .env    # defaults to http://localhost:8080/api, adjust if needed
npm install
npm run dev
```

Open `http://localhost:5173`.

---

## What to test first

1. Log in as each demo role and confirm you land on the correct dashboard.
2. As admin: view Students/Staff lists, open a student detail page, activate a staff member.
3. As staff: view "My Students", create a task assigned to a mentee, mark attendance.
4. As student: submit the task staff just created, check it shows "Submitted" then reflects
   staff's review once reviewed.
5. As admin: record a payment for a student and confirm the fee summary updates.
6. Register a brand-new student/staff account through the public registration pages and confirm
   a new staff account can't log in as ACTIVE until an admin activates it.

## Known gaps to flag to a reviewer

If this is being shown as a portfolio/interview project, be upfront that: file upload storage,
CSV/PDF report export, and real-time notification push are not implemented — call them out as
"next steps" rather than let someone discover the gap themselves.
