import { useEffect, useState } from 'react'
import { CalendarCheck, Users, ListChecks, CheckCircle2, Clock, FolderKanban } from 'lucide-react'
import { api } from '../../api/client'
import StatCard from '../../components/shared/StatCard'
import StatusBadge from '../../components/shared/StatusBadge'
import type { StaffDashboard } from '../../types'

export default function Dashboard() {
  const [data, setData] = useState<StaffDashboard | null>(null)

  useEffect(() => {
    api.get('/dashboard/staff').then((res) => setData(res.data)).catch(() => {})
  }, [])

  if (!data) return <p className="text-slate-500 text-sm">Loading dashboard...</p>

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Staff Dashboard</h1>
        <p className="text-sm text-slate-500">Overview of your students, tasks and salary.</p>
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="My Attendance" value={`${data.myAttendancePct}%`} icon={CalendarCheck} accent="blue" />
        <StatCard label="Students Assigned" value={data.studentsAssigned} icon={Users} accent="slate" />
        <StatCard label="Active Tasks" value={data.activeTasks} icon={ListChecks} accent="amber" />
        <StatCard label="Completed Tasks" value={data.completedTasks} icon={CheckCircle2} accent="green" />
        <StatCard label="Pending Tasks" value={data.pendingTasks} icon={Clock} accent="amber" />
        <StatCard label="Current Projects" value={data.currentProjects} icon={FolderKanban} accent="blue" />
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm inline-flex items-center gap-3">
        <span className="text-sm text-slate-500">Salary status this month:</span>
        <StatusBadge status={data.salaryStatusThisMonth} />
      </div>
    </div>
  )
}
