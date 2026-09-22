export type Role = 'ADMIN' | 'STAFF' | 'STUDENT'

export interface AuthUser {
  userId: number
  email: string
  role: Role
  status: string
  fullName: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface StudentSummary {
  id: number
  studentCode: string
  fullName: string
  photoUrl?: string
  email: string
  phone: string
  status: string
  mentorName?: string
  attendancePct: number
  taskCompletionPct: number
}

export interface StudentDetail {
  id: number
  studentCode: string
  fullName: string
  photoUrl?: string
  email: string
  dob: string
  gender?: string
  phone: string
  address?: string
  emergencyContact?: string
  qualification?: string
  college?: string
  status: string
  mentorId?: number
  mentorName?: string
  createdAt: string
}

export interface StaffSummary {
  id: number
  staffCode: string
  fullName: string
  photoUrl?: string
  email: string
  department: string
  designation: string
  active: boolean
  salaryBase: number
  skills: string[]
}

export interface StaffDetail extends StaffSummary {
  phone: string
  address?: string
  dateOfJoining: string
  qualification?: string
}

export interface Course {
  id: number
  name: string
  description?: string
  type: 'COURSE' | 'INTERNSHIP'
  durationMonths: number
  fee: number
  mentorId?: number
  mentorName?: string
  active: boolean
  modules: string[]
  enrolledCount: number
}

export interface AttendanceRecord {
  id: number
  date: string
  status: 'PRESENT' | 'ABSENT' | 'LEAVE' | 'HOLIDAY'
  approvalStatus: 'PENDING' | 'APPROVED' | 'REJECTED'
  checkIn?: string
  checkOut?: string
  remarks?: string
}

export interface PendingAttendance {
  id: number
  personId: number
  personName: string
  date: string
  status: 'PRESENT' | 'ABSENT' | 'LEAVE'
  checkIn?: string
  checkOut?: string
  remarks?: string
  requestedAt: string
}

export interface AttendanceSummary {
  totalWorkingDays: number
  presentDays: number
  absentDays: number
  leaveDays: number
  attendancePct: number
}

export interface Task {
  id: number
  title: string
  description: string
  category?: string
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  status: 'PENDING' | 'IN_PROGRESS' | 'SUBMITTED' | 'COMPLETED' | 'OVERDUE' | 'NEEDS_REVISION'
  assignedByStaffId: number
  assignedByName: string
  assignedByPhotoUrl?: string
  studentId: number
  studentName: string
  dueDate: string
  attachmentUrl?: string
  createdAt: string
}

export interface TaskSubmission {
  id: number
  textResponse?: string
  fileUrl?: string
  githubLink?: string
  demoLink?: string
  submittedAt: string
  staffFeedback?: string
  reviewedAt?: string
}

export interface Payment {
  id: number
  amount: number
  method: string
  transactionRef?: string
  paidAt: string
}

export interface Enrollment {
  id: number
  studentId: number
  studentName: string
  courseId: number
  courseName: string
  courseType: 'COURSE' | 'INTERNSHIP'
  startDate: string
  endDate: string
  progressPct: number
  totalFee: number
  createdAt: string
}

export interface FeeSummary {
  totalFee: number
  amountPaid: number
  remaining: number
  paymentStatus: string
}

export interface Salary {
  id: number
  salaryMonth: string
  amount: number
  status: 'PENDING' | 'PROCESSING' | 'PAID' | 'FAILED'
  paidAt?: string
  method?: string
  transactionRef?: string
  remarks?: string
}

export interface ProjectItem {
  id: number
  name: string
  description?: string
  technologies?: string
  startDate: string
  endDate?: string
  progressPct: number
  status: 'PLANNING' | 'IN_PROGRESS' | 'ON_HOLD' | 'COMPLETED' | 'ARCHIVED'
  githubLink?: string
  liveLink?: string
  memberNames: string[]
}

export interface NotificationItem {
  id: number
  title: string
  body: string
  read: boolean
  createdAt: string
}

export interface StudentDashboard {
  attendancePct: number
  totalTasks: number
  completedTasks: number
  pendingTasks: number
  currentCourseName?: string
  feePaid: number
  feeRemaining: number
}

export interface StaffDashboard {
  myAttendancePct: number
  studentsAssigned: number
  activeTasks: number
  completedTasks: number
  pendingTasks: number
  currentProjects: number
  salaryStatusThisMonth: string
}

export interface AdminDashboard {
  totalStudents: number
  activeStudents: number
  completedStudents: number
  totalStaff: number
  activeStaff: number
  todaysAttendanceMarked: number
  pendingTasks: number
  completedTasks: number
  totalRevenue: number
  salaryPendingCount: number
}
