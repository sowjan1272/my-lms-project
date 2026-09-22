import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, getApiErrorMessage } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import Toast from '../../components/shared/Toast'
import { formatCurrency } from '../../lib/utils'
import type { StaffSummary, PageResponse } from '../../types'

export default function Staff() {
  const navigate = useNavigate()
  const [staff, setStaff] = useState<StaffSummary[]>([])
  const [toast, setToast] = useState<string | null>(null)

  function load(q = '') {
    api.get<PageResponse<StaffSummary>>('/staff', { params: { search: q, size: 100 } })
      .then((res) => setStaff(res.data.content)).catch(() => {})
  }

  useEffect(() => load(), [])

  async function toggleActive(id: number, active: boolean) {
    try {
      await api.patch(`/staff/${id}/active`, { active: !active })
      setToast(!active ? 'Staff account activated.' : 'Staff account deactivated.')
      load()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to update staff status.'))
    }
  }

  const columns: Column<StaffSummary>[] = [
    {
      header: 'Staff', accessor: (s) => (
        <div className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center text-xs font-semibold">
            {s.fullName.charAt(0)}
          </div>
          <div>
            <p className="font-medium text-slate-800">{s.fullName}</p>
            <p className="text-xs text-slate-400">{s.staffCode}</p>
          </div>
        </div>
      ),
    },
    { header: 'Email', accessor: (s) => s.email },
    { header: 'Department', accessor: (s) => s.department },
    { header: 'Designation', accessor: (s) => s.designation },
    { header: 'Salary', accessor: (s) => formatCurrency(s.salaryBase) },
    { header: 'Status', accessor: (s) => <StatusBadge status={s.active ? 'ACTIVE' : 'INACTIVE'} /> },
    {
      header: '', accessor: (s) => (
        <button
          onClick={(e) => { e.stopPropagation(); toggleActive(s.id, s.active) }}
          className="text-xs font-medium text-brand-600 hover:underline"
        >
          {s.active ? 'Deactivate' : 'Activate'}
        </button>
      ),
    },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Staff</h1>
        <p className="text-sm text-slate-500">Manage all staff members. New registrations need activation here.</p>
      </div>

      <DataTable columns={columns} data={staff} rowKey={(s) => s.id} searchable onSearch={load} emptyMessage="No staff found."
        onRowClick={(s) => navigate(`/admin/staff/${s.id}`)} />

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}
