import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './contexts/AuthContext'
import ProtectedRoute from './routes/ProtectedRoute'
import AppLayout from './components/layout/AppLayout'

import Login from './pages/auth/Login'
import RegisterStudent from './pages/auth/RegisterStudent'
import RegisterStaff from './pages/auth/RegisterStaff'

import AdminDashboard from './pages/admin/Dashboard'
import AdminStudents from './pages/admin/Students'
import AdminStudentDetail from './pages/admin/StudentDetail'
import AdminStaff from './pages/admin/Staff'
import AdminStaffDetail from './pages/admin/StaffDetails'
import AdminCourses from './pages/admin/Courses'
import AdminAttendance from './pages/admin/Attendance'
import AdminTasks from './pages/admin/Tasks'
import AdminPayments from './pages/admin/Payments'
import AdminSalaries from './pages/admin/Salaries'
import AdminProjects from './pages/admin/Projects'
import AdminReports from './pages/admin/Reports'
import AdminSettings from './pages/admin/Settings'

import StaffDashboard from './pages/staff/Dashboard'
import StaffStudents from './pages/staff/Students'
import StaffStudentDetail from './pages/staff/StudentDetail'
import StaffTasks from './pages/staff/Tasks'
import StaffAttendance from './pages/staff/Attendance'
import StaffSalary from './pages/staff/Salary'
import StaffProjects from './pages/staff/Projects'
import StaffProfile from './pages/staff/Profile'

import StudentDashboard from './pages/student/Dashboard'
import StudentAttendance from './pages/student/Attendance'
import StudentTasks from './pages/student/Tasks'
import StudentCourse from './pages/student/Course'
import StudentPayments from './pages/student/Payments'
import StudentProjects from './pages/student/Projects'
import StudentProfile from './pages/student/Profile'

function RoleHome() {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  return <Navigate to={`/${user.role.toLowerCase()}/dashboard`} replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register/student" element={<RegisterStudent />} />
      <Route path="/register/staff" element={<RegisterStaff />} />

      {/* Admin */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route path="dashboard" element={<AdminDashboard />} />
        <Route path="students" element={<AdminStudents />} />
        <Route path="students/:id" element={<AdminStudentDetail />} />
        <Route path="staff" element={<AdminStaff />} />
        <Route path="staff/:id" element={<AdminStaffDetail />} />
        <Route path="courses" element={<AdminCourses />} />
        <Route path="attendance" element={<AdminAttendance />} />
        <Route path="tasks" element={<AdminTasks />} />
        <Route path="payments" element={<AdminPayments />} />
        <Route path="salaries" element={<AdminSalaries />} />
        <Route path="projects" element={<AdminProjects />} />
        <Route path="reports" element={<AdminReports />} />
        <Route path="settings" element={<AdminSettings />} />
      </Route>

      {/* Staff */}
      <Route
        path="/staff"
        element={
          <ProtectedRoute allowedRoles={['STAFF']}>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route path="dashboard" element={<StaffDashboard />} />
        <Route path="students" element={<StaffStudents />} />
        <Route path="students/:id" element={<StaffStudentDetail />} />
        <Route path="tasks" element={<StaffTasks />} />
        <Route path="attendance" element={<StaffAttendance />} />
        <Route path="salary" element={<StaffSalary />} />
        <Route path="projects" element={<StaffProjects />} />
        <Route path="profile" element={<StaffProfile />} />
      </Route>

      {/* Student */}
      <Route
        path="/student"
        element={
          <ProtectedRoute allowedRoles={['STUDENT']}>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route path="dashboard" element={<StudentDashboard />} />
        <Route path="attendance" element={<StudentAttendance />} />
        <Route path="tasks" element={<StudentTasks />} />
        <Route path="course" element={<StudentCourse />} />
        <Route path="payments" element={<StudentPayments />} />
        <Route path="projects" element={<StudentProjects />} />
        <Route path="profile" element={<StudentProfile />} />
      </Route>

      <Route path="/" element={<RoleHome />} />
      <Route path="*" element={<RoleHome />} />
    </Routes>
  )
}
