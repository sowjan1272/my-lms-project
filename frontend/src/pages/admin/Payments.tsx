import { useEffect, useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import Toast from '../../components/shared/Toast'
import DataTable, { Column } from '../../components/shared/DataTable'
import { formatCurrency, formatDate } from '../../lib/utils'
import type { StudentSummary, PageResponse, Payment } from '../../types'

export default function Payments() {
  const [students, setStudents] = useState<StudentSummary[]>([])
  const [selectedStudent, setSelectedStudent] = useState('')
  const [payments, setPayments] = useState<Payment[]>([])
  const [toast, setToast] = useState<string | null>(null)
  const [form, setForm] = useState({ amount: '', method: 'UPI', transactionRef: '' })

  useEffect(() => {
    api.get<PageResponse<StudentSummary>>('/students', { params: { size: 200 } }).then((res) => setStudents(res.data.content)).catch(() => {})
  }, [])

  useEffect(() => {
    if (selectedStudent) {
      api.get(`/payments/student/${selectedStudent}`).then((res) => setPayments(res.data)).catch(() => {})
    } else {
      setPayments([])
    }
  }, [selectedStudent])

  async function handleRecord(e: React.FormEvent) {
    e.preventDefault()
    if (!selectedStudent) {
      setToast('Select a student first.')
      return
    }
    try {
      await api.post('/payments', {
        studentId: Number(selectedStudent), amount: Number(form.amount),
        method: form.method, transactionRef: form.transactionRef,
      })
      setToast('Payment recorded successfully.')
      setForm({ amount: '', method: 'UPI', transactionRef: '' })
      const res = await api.get(`/payments/student/${selectedStudent}`)
      setPayments(res.data)
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to record payment.'))
    }
  }

  const columns: Column<Payment>[] = [
    { header: 'Date', accessor: (p) => formatDate(p.paidAt) },
    { header: 'Amount', accessor: (p) => formatCurrency(p.amount) },
    { header: 'Method', accessor: (p) => p.method },
    { header: 'Reference', accessor: (p) => p.transactionRef || '—' },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Fee & Payments</h1>
        <p className="text-sm text-slate-500">Record and review student payments.</p>
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        <form onSubmit={handleRecord} className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm space-y-4 h-fit">
          <select required value={selectedStudent} onChange={(e) => setSelectedStudent(e.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm">
            <option value="">Select student</option>
            {students.map((s) => <option key={s.id} value={s.id}>{s.fullName} ({s.studentCode})</option>)}
          </select>
          <input required type="number" placeholder="Amount" value={form.amount}
            onChange={(e) => setForm((f) => ({ ...f, amount: e.target.value }))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
          <select value={form.method} onChange={(e) => setForm((f) => ({ ...f, method: e.target.value }))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm">
            <option>UPI</option>
            <option>Bank Transfer</option>
            <option>Card</option>
            <option>Cash</option>
          </select>
          <input placeholder="Transaction reference (optional)" value={form.transactionRef}
            onChange={(e) => setForm((f) => ({ ...f, transactionRef: e.target.value }))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
          <button type="submit" className="w-full bg-brand-600 hover:bg-brand-700 text-white rounded-lg py-2.5 text-sm font-medium">
            Record Payment
          </button>
        </form>

        <div className="lg:col-span-2">
          <DataTable columns={columns} data={payments} rowKey={(p) => p.id} emptyMessage="Select a student to view payment history." />
        </div>
      </div>

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}
