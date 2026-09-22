import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api, getApiErrorMessage } from '../../api/client'
import StatusBadge from '../../components/shared/StatusBadge'
import Toast from '../../components/shared/Toast'
import { formatCurrency, formatDate } from '../../lib/utils'
import type { StaffDetail as StaffDetailType, Salary } from '../../types'

export default function StaffDetail() {
  const { id } = useParams()
  const [staff, setStaff] = useState<StaffDetailType | null>(null)
  const [salaries, setSalaries] = useState<Salary[]>([])
  const [toast, setToast] = useState<string | null>(null)

  function load() {
    if (!id) return
    api.get(`/staff/${id}`).then((res) => setStaff(res.data)).catch(() => {})
    api.get(`/salaries/staff/${id}`).then((res) => setSalaries(res.data)).catch(() => {})
  }

  useEffect(load, [id])

  async function toggleActive() {
    if (!staff) return
    try {
      await api.patch(`/staff/${staff.id}/active`, { active: !staff.active })
      setToast(!staff.active ? 'Staff account activated.' : 'Staff account deactivated.')
      load()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to update staff status.'))
    }
  }

  if (!staff) return <p className="text-sm text-slate-400">Loading...</p>

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-4">
          <div className="w-14 h-14 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center text-lg font-semibold">
            {staff.fullName.charAt(0)}
          </div>
          <div>
            <h1 className="text-xl font-semibold text-slate-900">{staff.fullName}</h1>
            <p className="text-sm text-slate-500">{staff.staffCode} · {staff.designation}</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <StatusBadge status={staff.active ? 'ACTIVE' : 'INACTIVE'} />
          <button onClick={toggleActive} className="text-sm font-medium text-brand-600 hover:underline">
            {staff.active ? 'Deactivate' : 'Activate'}
          </button>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm grid sm:grid-cols-2 gap-4 text-sm">
        <div><p className="text-xs text-slate-400">Email</p><p className="text-slate-800">{staff.email}</p></div>
        <div><p className="text-xs text-slate-400">Phone</p><p className="text-slate-800">{staff.phone || '—'}</p></div>
        <div><p className="text-xs text-slate-400">Department</p><p className="text-slate-800">{staff.department || '—'}</p></div>
        <div><p className="text-xs text-slate-400">Designation</p><p className="text-slate-800">{staff.designation || '—'}</p></div>
        <div><p className="text-xs text-slate-400">Date of Joining</p><p className="text-slate-800">{staff.dateOfJoining ? formatDate(staff.dateOfJoining) : '—'}</p></div>
        <div><p className="text-xs text-slate-400">Base Salary</p><p className="text-slate-800">{formatCurrency(staff.salaryBase)}</p></div>
        <div><p className="text-xs text-slate-400">Qualification</p><p className="text-slate-800">{staff.qualification || '—'}</p></div>
        <div className="sm:col-span-2"><p className="text-xs text-slate-400">Address</p><p className="text-slate-800">{staff.address || '—'}</p></div>
        {staff.skills.length > 0 && (
          <div className="sm:col-span-2">
            <p className="text-xs text-slate-400 mb-1">Skills</p>
            <div className="flex flex-wrap gap-1.5">
              {staff.skills.map((s) => (
                <span key={s} className="text-xs bg-slate-100 text-slate-600 rounded-full px-2.5 py-1">{s}</span>
              ))}
            </div>
          </div>
        )}
      </div>

      <div>
        <h2 className="text-sm font-semibold text-slate-700 mb-3">Salary History</h2>
        {salaries.length === 0 ? (
          <p className="text-sm text-slate-400">No salary payments recorded yet.</p>
        ) : (
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm divide-y divide-slate-100">
            {salaries.map((sp) => (
              <div key={sp.id} className="p-4 flex items-center justify-between text-sm">
                <div>
                  <p className="font-medium text-slate-800">{sp.salaryMonth}</p>
                  <p className="text-xs text-slate-500">{sp.method || 'Not paid yet'}</p>
                </div>
                <div className="flex items-center gap-3">
                  <span className="font-medium">{formatCurrency(sp.amount)}</span>
                  <StatusBadge status={sp.status} />
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}
