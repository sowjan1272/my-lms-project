import { NavLink } from 'react-router-dom'
import { useState } from 'react'
import {
  LayoutDashboard, Users, UserCog, BookOpen, GraduationCap, CalendarCheck,
  ListChecks, Wallet, Banknote, FolderKanban, Bell, FileBarChart, Settings,
  Menu, X, User,
} from 'lucide-react'
import { useAuth } from '../../contexts/AuthContext'
import type { Role } from '../../types'

interface NavItem {
  label: string
  to: string
  icon: React.ElementType
}

const NAV_ITEMS: Record<Role, NavItem[]> = {
  ADMIN: [
    { label: 'Dashboard', to: '/admin/dashboard', icon: LayoutDashboard },
    { label: 'Students', to: '/admin/students', icon: Users },
    { label: 'Staff', to: '/admin/staff', icon: UserCog },
    { label: 'Courses', to: '/admin/courses', icon: BookOpen },
    { label: 'Attendance', to: '/admin/attendance', icon: CalendarCheck },
    { label: 'Tasks', to: '/admin/tasks', icon: ListChecks },
    { label: 'Payments', to: '/admin/payments', icon: Wallet },
    { label: 'Salaries', to: '/admin/salaries', icon: Banknote },
    { label: 'Projects', to: '/admin/projects', icon: FolderKanban },
    { label: 'Reports', to: '/admin/reports', icon: FileBarChart },
    { label: 'Settings', to: '/admin/settings', icon: Settings },
  ],
  STAFF: [
    { label: 'Dashboard', to: '/staff/dashboard', icon: LayoutDashboard },
    { label: 'My Students', to: '/staff/students', icon: Users },
    { label: 'Tasks', to: '/staff/tasks', icon: ListChecks },
    { label: 'Attendance', to: '/staff/attendance', icon: CalendarCheck },
    { label: 'Salary', to: '/staff/salary', icon: Banknote },
    { label: 'Projects', to: '/staff/projects', icon: FolderKanban },
    { label: 'Profile', to: '/staff/profile', icon: User },
  ],
  STUDENT: [
    { label: 'Dashboard', to: '/student/dashboard', icon: LayoutDashboard },
    { label: 'My Attendance', to: '/student/attendance', icon: CalendarCheck },
    { label: 'My Tasks', to: '/student/tasks', icon: ListChecks },
    { label: 'Course', to: '/student/course', icon: GraduationCap },
    { label: 'Payments', to: '/student/payments', icon: Wallet },
    { label: 'Projects', to: '/student/projects', icon: FolderKanban },
    { label: 'Profile', to: '/student/profile', icon: User },
  ],
}

export default function Sidebar() {
  const { user } = useAuth()
  const [open, setOpen] = useState(false)
  if (!user) return null

  const items = NAV_ITEMS[user.role]

  return (
    <>
      {/* Mobile hamburger */}
      <button
        className="md:hidden fixed top-4 left-4 z-40 bg-white border border-slate-200 rounded-lg p-2 shadow-sm"
        onClick={() => setOpen(true)}
        aria-label="Open menu"
      >
        <Menu size={20} />
      </button>

      {/* Mobile overlay */}
      {open && (
        <div className="md:hidden fixed inset-0 bg-black/40 z-40" onClick={() => setOpen(false)} />
      )}

      <aside
        className={`fixed md:sticky top-0 left-0 h-screen w-64 bg-white border-r border-slate-200 flex flex-col z-50 transition-transform
        ${open ? 'translate-x-0' : '-translate-x-full'} md:translate-x-0`}
      >
        <div className="h-16 flex items-center justify-between px-5 border-b border-slate-100">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-brand-600 text-white flex items-center justify-center font-bold text-sm">
              LMS
            </div>
            <span className="font-semibold text-slate-800 text-sm">Company Portal</span>
          </div>
          <button className="md:hidden text-slate-500" onClick={() => setOpen(false)}>
            <X size={20} />
          </button>
        </div>

        <nav className="flex-1 overflow-y-auto py-4 px-3 space-y-1">
          {items.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              onClick={() => setOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive ? 'bg-brand-50 text-brand-700' : 'text-slate-600 hover:bg-slate-50'
                }`
              }
            >
              <item.icon size={18} />
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>
    </>
  )
}
