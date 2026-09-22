import { useEffect, useState } from 'react'
import { Users, UserCog, CalendarCheck, ListChecks, CheckCircle2, Wallet, Banknote, GraduationCap } from 'lucide-react'
import { api } from '../../api/client'
import StatCard from '../../components/shared/StatCard'
import { formatCurrency } from '../../lib/utils'
import type { AdminDashboard } from '../../types'

export default function Dashboard() {
  const [data, setData] = useState<AdminDashboard | null>(null)

  useEffect(() => {
    api.get('/dashboard/admin').then((res) => setData(res.data)).catch(() => {})
  }, [])

  if (!data) return <p className="text-slate-500 text-sm">Loading dashboard...</p>

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Admin Dashboard</h1>
        <p className="text-sm text-slate-500">System-wide overview.</p>
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="Total Students" value={data.totalStudents} icon={Users} accent="blue" />
        <StatCard label="Active Students" value={data.activeStudents} icon={GraduationCap} accent="green" />
        <StatCard label="Completed Students" value={data.completedStudents} icon={CheckCircle2} accent="slate" />
        <StatCard label="Total Staff" value={data.totalStaff} icon={UserCog} accent="blue" />
        <StatCard label="Active Staff" value={data.activeStaff} icon={UserCog} accent="green" />
        <StatCard label="Today's Attendance" value={data.todaysAttendanceMarked} icon={CalendarCheck} accent="amber" />
        <StatCard label="Pending Tasks" value={data.pendingTasks} icon={ListChecks} accent="amber" />
        <StatCard label="Completed Tasks" value={data.completedTasks} icon={CheckCircle2} accent="green" />
      </div>

      <div className="grid sm:grid-cols-2 gap-4">
        <StatCard label="Total Revenue" value={formatCurrency(data.totalRevenue)} icon={Wallet} accent="green" />
        <StatCard label="Salaries Pending" value={data.salaryPendingCount} icon={Banknote} accent="red" />
      </div>
    </div>
  )
}
