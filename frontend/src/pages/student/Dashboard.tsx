import { useEffect, useState } from 'react'
import { CalendarCheck, ListChecks, CheckCircle2, Clock, GraduationCap, Wallet } from 'lucide-react'
import { api } from '../../api/client'
import StatCard from '../../components/shared/StatCard'
import { formatCurrency } from '../../lib/utils'
import type { StudentDashboard } from '../../types'

export default function Dashboard() {
  const [data, setData] = useState<StudentDashboard | null>(null)

  useEffect(() => {
    api.get('/dashboard/student').then((res) => setData(res.data)).catch(() => {})
  }, [])

  if (!data) return <p className="text-slate-500 text-sm">Loading dashboard...</p>

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">My Dashboard</h1>
        <p className="text-sm text-slate-500">Here's an overview of your progress.</p>
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="Attendance" value={`${data.attendancePct}%`} icon={CalendarCheck} accent="blue" />
        <StatCard label="Total Tasks" value={data.totalTasks} icon={ListChecks} accent="slate" />
        <StatCard label="Completed Tasks" value={data.completedTasks} icon={CheckCircle2} accent="green" />
        <StatCard label="Pending Tasks" value={data.pendingTasks} icon={Clock} accent="amber" />
      </div>

      <div className="grid sm:grid-cols-2 gap-4">
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <div className="flex items-center gap-2 text-slate-500 text-sm mb-2">
            <GraduationCap size={18} /> Current Course
          </div>
          <p className="text-lg font-semibold text-slate-900">{data.currentCourseName || 'Not enrolled yet'}</p>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <div className="flex items-center gap-2 text-slate-500 text-sm mb-2">
            <Wallet size={18} /> Fees
          </div>
          <p className="text-sm text-slate-600">
            Paid: <span className="font-semibold text-emerald-600">{formatCurrency(data.feePaid)}</span>
            {' · '}
            Remaining: <span className="font-semibold text-amber-600">{formatCurrency(data.feeRemaining)}</span>
          </p>
        </div>
      </div>
    </div>
  )
}
