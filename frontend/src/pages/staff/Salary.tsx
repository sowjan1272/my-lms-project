import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import { formatCurrency, formatDate } from '../../lib/utils'
import type { Salary as SalaryType } from '../../types'

export default function Salary() {
  const [history, setHistory] = useState<SalaryType[]>([])

  useEffect(() => {
    api.get('/salaries/staff/me').then((res) => setHistory(res.data)).catch(() => {})
  }, [])

  const columns: Column<SalaryType>[] = [
    { header: 'Month', accessor: (s) => s.salaryMonth },
    { header: 'Amount', accessor: (s) => formatCurrency(s.amount) },
    { header: 'Status', accessor: (s) => <StatusBadge status={s.status} /> },
    { header: 'Paid On', accessor: (s) => (s.paidAt ? formatDate(s.paidAt) : '—') },
    { header: 'Method', accessor: (s) => s.method || '—' },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">My Salary</h1>
        <p className="text-sm text-slate-500">Your monthly salary payment history.</p>
      </div>
      <DataTable columns={columns} data={history} rowKey={(s) => s.id} emptyMessage="No salary records available yet." />
    </div>
  )
}
