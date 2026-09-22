import { useEffect, useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import Toast from '../../components/shared/Toast'
import type { StudentSummary, StaffSummary, PageResponse, PendingAttendance } from '../../types'

export default function Attendance() {
  const [students, setStudents] = useState<StudentSummary[]>([])
  const [staff, setStaff] = useState<StaffSummary[]>([])
  const [pending, setPending] = useState<PendingAttendance[]>([])
  const [toast, setToast] = useState<string | null>(null)
  const [form, setForm] = useState({ personType: 'student', personId: '', date: new Date().toISOString().slice(0, 10), status: 'PRESENT', remarks: '' })

  function loadPending() {
    api.get('/attendance/pending').then((res) => setPending(res.data)).catch(() => {})
  }

  useEffect(() => {
    api.get<PageResponse<StudentSummary>>('/students', { params: { size: 200 } }).then((res) => setStudents(res.data.content)).catch(() => {})
    api.get<PageResponse<StaffSummary>>('/staff', { params: { size: 200 } }).then((res) => setStaff(res.data.content)).catch(() => {})
    loadPending()
  }, [])

  async function handleReview(id: number, action: 'approve' | 'reject') {
    try {
      await api.patch(`/attendance/${id}/${action}`)
      setToast(action === 'approve' ? 'Attendance approved.' : 'Attendance rejected.')
      loadPending()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to update attendance.'))
    }
  }

  async function handleMark(e: React.FormEvent) {
    e.preventDefault()
    if (!form.personId) {
      setToast('Select a person first.')
      return
    }
    try {
      const payload: any = { date: form.date, status: form.status, remarks: form.remarks }
      if (form.personType === 'student') payload.studentId = Number(form.personId)
      else payload.staffId = Number(form.personId)
      await api.post('/attendance', payload)
      setToast('Attendance marked.')
      setForm((f) => ({ ...f, personId: '', remarks: '' }))
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to mark attendance.'))
    }
  }

  return (
    <div className="space-y-6 max-w-lg">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Attendance Management</h1>
        <p className="text-sm text-slate-500">Mark daily attendance for a student or staff member.</p>
      </div>

      {pending.length > 0 && (
        <div className="bg-amber-50 border border-amber-200 rounded-xl p-5">
          <h2 className="text-sm font-semibold text-amber-900 mb-3">Pending Staff Approvals ({pending.length})</h2>
          <div className="space-y-2">
            {pending.map((p) => (
              <div key={p.id} className="bg-white rounded-lg border border-amber-100 p-3 flex items-center justify-between text-sm">
                <div>
                  <p className="font-medium text-slate-800">{p.personName}</p>
                  <p className="text-xs text-slate-500">{p.date} · {p.status}{p.remarks ? ` · ${p.remarks}` : ''}</p>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleReview(p.id, 'approve')} className="text-xs font-medium text-emerald-700 bg-emerald-50 hover:bg-emerald-100 rounded-lg px-3 py-1.5">
                    Approve
                  </button>
                  <button onClick={() => handleReview(p.id, 'reject')} className="text-xs font-medium text-red-700 bg-red-50 hover:bg-red-100 rounded-lg px-3 py-1.5">
                    Reject
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      <form onSubmit={handleMark} className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm space-y-4">
        <div className="flex gap-4 text-sm">
          <label className="flex items-center gap-2">
            <input type="radio" checked={form.personType === 'student'}
              onChange={() => setForm((f) => ({ ...f, personType: 'student', personId: '' }))} />
            Student
          </label>
          <label className="flex items-center gap-2">
            <input type="radio" checked={form.personType === 'staff'}
              onChange={() => setForm((f) => ({ ...f, personType: 'staff', personId: '' }))} />
            Staff
          </label>
        </div>

        <select
          required
          value={form.personId}
          onChange={(e) => setForm((f) => ({ ...f, personId: e.target.value }))}
          className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
        >
          <option value="">Select {form.personType}</option>
          {(form.personType === 'student' ? students : staff).map((p) => (
            <option key={p.id} value={p.id}>{p.fullName}</option>
          ))}
        </select>

        <div className="grid grid-cols-2 gap-3">
          <input type="date" value={form.date} onChange={(e) => setForm((f) => ({ ...f, date: e.target.value }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm" />
          <select value={form.status} onChange={(e) => setForm((f) => ({ ...f, status: e.target.value }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm">
            <option value="PRESENT">Present</option>
            <option value="ABSENT">Absent</option>
            <option value="LEAVE">Leave</option>
            <option value="HOLIDAY">Holiday</option>
          </select>
        </div>

        <input placeholder="Remarks (optional)" value={form.remarks}
          onChange={(e) => setForm((f) => ({ ...f, remarks: e.target.value }))}
          className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />

        <button type="submit" className="w-full bg-brand-600 hover:bg-brand-700 text-white rounded-lg py-2.5 text-sm font-medium">
          Mark Attendance
        </button>
      </form>

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}
