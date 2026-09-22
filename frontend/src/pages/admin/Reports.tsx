import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import StatCard from '../../components/shared/StatCard'
import { formatCurrency } from '../../lib/utils'
import type { AdminDashboard } from '../../types'
import { Users, UserCog, Wallet, Banknote } from 'lucide-react'

// Reports MVP: surfaces the same aggregate stats as the dashboard endpoint, presented
// as a reports summary. CSV/PDF export and per-filter drill-down are not implemented yet.
export default function Reports() {
  const [data, setData] = useState<AdminDashboard | null>(null)

  useEffect(() => {
    api.get('/dashboard/admin').then((res) => setData(res.data)).catch(() => {})
  }, [])

  if (!data) return <p className="text-sm text-slate-500">Loading reports...</p>

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Reports</h1>
        <p className="text-sm text-slate-500">High-level summary reports. Filtered and exportable reports are on the roadmap.</p>
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="Total Students" value={data.totalStudents} icon={Users} accent="blue" />
        <StatCard label="Total Staff" value={data.totalStaff} icon={UserCog} accent="blue" />
        <StatCard label="Total Revenue" value={formatCurrency(data.totalRevenue)} icon={Wallet} accent="green" />
        <StatCard label="Salaries Pending" value={data.salaryPendingCount} icon={Banknote} accent="red" />
      </div>
    </div>
  )
}
