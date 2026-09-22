import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatCard from '../../components/shared/StatCard'
import { formatCurrency, formatDate } from '../../lib/utils'
import type { Payment, FeeSummary } from '../../types'
import { Wallet, CheckCircle2, Clock } from 'lucide-react'

export default function Payments() {
  const [payments, setPayments] = useState<Payment[]>([])
  const [summary, setSummary] = useState<FeeSummary | null>(null)

  useEffect(() => {
    api.get('/payments/student/me').then((res) => setPayments(res.data)).catch(() => {})
    api.get('/payments/student/me/summary').then((res) => setSummary(res.data)).catch(() => {})
  }, [])

  const columns: Column<Payment>[] = [
    { header: 'Date', accessor: (p) => formatDate(p.paidAt) },
    { header: 'Amount', accessor: (p) => formatCurrency(p.amount) },
    { header: 'Method', accessor: (p) => p.method },
    { header: 'Reference', accessor: (p) => p.transactionRef || '—' },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Payments</h1>
        <p className="text-sm text-slate-500">Your fee payment history.</p>
      </div>

      {summary && (
        <div className="grid sm:grid-cols-3 gap-4">
          <StatCard label="Total Fee" value={formatCurrency(summary.totalFee)} icon={Wallet} accent="slate" />
          <StatCard label="Amount Paid" value={formatCurrency(summary.amountPaid)} icon={CheckCircle2} accent="green" />
          <StatCard label="Remaining" value={formatCurrency(summary.remaining)} icon={Clock} accent="amber" />
        </div>
      )}

      <DataTable columns={columns} data={payments} rowKey={(p) => p.id} emptyMessage="No payment history available." />
    </div>
  )
}
