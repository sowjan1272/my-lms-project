import { useEffect, useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import Toast from '../../components/shared/Toast'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import { formatCurrency, formatDate } from '../../lib/utils'
import type { StaffSummary, PageResponse, Salary } from '../../types'

export default function Salaries() {
  const [staffList, setStaffList] = useState<StaffSummary[]>([])
  const [selectedStaff, setSelectedStaff] = useState('')
  const [history, setHistory] = useState<Salary[]>([])
  const [toast, setToast] = useState<string | null>(null)
  const [form, setForm] = useState({ month: new Date().toISOString().slice(0, 7), amount: '' })

  useEffect(() => {
    api.get<PageResponse<StaffSummary>>('/staff', { params: { size: 200 } }).then((res) => setStaffList(res.data.content)).catch(() => {})
  }, [])

  function loadHistory(staffId: string) {
    if (staffId) api.get(`/salaries/staff/${staffId}`).then((res) => setHistory(res.data)).catch(() => {})
    else setHistory([])
  }

  useEffect(() => loadHistory(selectedStaff), [selectedStaff])

  async function handleGenerate(e: React.FormEvent) {
    e.preventDefault()
    if (!selectedStaff) { setToast('Select a staff member first.'); return }
    try {
      await api.post('/salaries', { staffId: Number(selectedStaff), salaryMonth: form.month, amount: Number(form.amount) })
      setToast('Salary record created.')
      loadHistory(selectedStaff)
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to create salary record.'))
    }
  }

  async function markPaid(id: number) {
    try {
      await api.patch(`/salaries/${id}/status`, { status: 'PAID', method: 'Bank Transfer' })
      setToast('Salary marked as paid.')
      loadHistory(selectedStaff)
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to update salary status.'))
    }
  }

  const columns: Column<Salary>[] = [
    { header: 'Month', accessor: (s) => s.salaryMonth },
    { header: 'Amount', accessor: (s) => formatCurrency(s.amount) },
    { header: 'Status', accessor: (s) => <StatusBadge status={s.status} /> },
    { header: 'Paid On', accessor: (s) => (s.paidAt ? formatDate(s.paidAt) : '—') },
    {
      header: '', accessor: (s) => (
        s.status !== 'PAID' ? (
          <button onClick={() => markPaid(s.id)} className="text-xs font-medium text-brand-600 hover:underline">Mark Paid</button>
        ) : null
      ),
    },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Salary Management</h1>
        <p className="text-sm text-slate-500">Generate and track staff salary payments.</p>
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        <form onSubmit={handleGenerate} className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm space-y-4 h-fit">
          <select required value={selectedStaff} onChange={(e) => setSelectedStaff(e.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm">
            <option value="">Select staff</option>
            {staffList.map((s) => <option key={s.id} value={s.id}>{s.fullName} ({s.staffCode})</option>)}
          </select>
          <input required type="month" value={form.month} onChange={(e) => setForm((f) => ({ ...f, month: e.target.value }))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
          <input required type="number" placeholder="Amount" value={form.amount}
            onChange={(e) => setForm((f) => ({ ...f, amount: e.target.value }))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
          <button type="submit" className="w-full bg-brand-600 hover:bg-brand-700 text-white rounded-lg py-2.5 text-sm font-medium">
            Generate Salary Record
          </button>
        </form>

        <div className="lg:col-span-2">
          <DataTable columns={columns} data={history} rowKey={(s) => s.id} emptyMessage="Select a staff member to view salary history." />
        </div>
      </div>

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}
