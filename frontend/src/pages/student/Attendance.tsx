import { useEffect, useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import StatCard from '../../components/shared/StatCard'
import Toast from '../../components/shared/Toast'
import type { AttendanceRecord, AttendanceSummary } from '../../types'
import { CalendarCheck, CalendarX, CalendarClock } from 'lucide-react'

function monthRange(date: Date) {
  const from = new Date(date.getFullYear(), date.getMonth(), 1)
  const to = new Date(date.getFullYear(), date.getMonth() + 1, 0)
  return { from: from.toISOString().slice(0, 10), to: to.toISOString().slice(0, 10) }
}

const STATUS_STYLES: Record<string, string> = {
  PRESENT: 'bg-emerald-500 text-white',
  ABSENT: 'bg-red-100 text-red-700',
  LEAVE: 'bg-blue-100 text-blue-700',
  HOLIDAY: 'bg-slate-200 text-slate-600',
}

const APPROVAL_RING: Record<string, string> = {
  PENDING: 'ring-2 ring-amber-400 ring-offset-1',
  REJECTED: 'ring-2 ring-red-500 ring-offset-1',
  APPROVED: '',
}

export default function Attendance() {
  const [records, setRecords] = useState<AttendanceRecord[]>([])
  const [summary, setSummary] = useState<AttendanceSummary | null>(null)
  const [selected, setSelected] = useState<AttendanceRecord | null>(null)
  const [month] = useState(new Date())
  const [showMark, setShowMark] = useState(false)
  const [toast, setToast] = useState<string | null>(null)
  const [form, setForm] = useState({
    date: new Date().toISOString().slice(0, 10),
    status: 'PRESENT',
    checkIn: '', checkOut: '', remarks: '',
  })

  function load() {
    const { from, to } = monthRange(month)
    api.get('/attendance/student/me', { params: { from, to } }).then((res) => setRecords(res.data)).catch(() => {})
    api.get('/attendance/student/me/summary').then((res) => setSummary(res.data)).catch(() => {})
  }

  useEffect(load, [])

  async function handleMark(e: React.FormEvent) {
    e.preventDefault()
    try {
      await api.post('/attendance/self/student', {
        date: form.date, status: form.status,
        checkIn: form.checkIn || undefined, checkOut: form.checkOut || undefined,
        remarks: form.remarks || undefined,
      })
      setToast('Attendance submitted for your mentor\'s approval.')
      setShowMark(false)
      load()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to submit attendance.'))
    }
  }

  const recordByDate = new Map(records.map((r) => [r.date, r]))
  const daysInMonth = new Date(month.getFullYear(), month.getMonth() + 1, 0).getDate()
  const firstWeekday = new Date(month.getFullYear(), month.getMonth(), 1).getDay()

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-900">My Attendance</h1>
          <p className="text-sm text-slate-500">Track your daily attendance for this month.</p>
        </div>
        <button onClick={() => setShowMark(true)} className="bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium">
          Mark My Attendance
        </button>
      </div>

      {summary && (
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <StatCard label="Attendance %" value={`${summary.attendancePct}%`} icon={CalendarCheck} accent="blue" />
          <StatCard label="Present Days" value={summary.presentDays} icon={CalendarCheck} accent="green" />
          <StatCard label="Absent Days" value={summary.absentDays} icon={CalendarX} accent="red" />
          <StatCard label="Leave Days" value={summary.leaveDays} icon={CalendarClock} accent="amber" />
        </div>
      )}

      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm max-w-3xl">
        <p className="font-semibold text-slate-800 mb-5 text-lg">
          {month.toLocaleDateString('en-IN', { month: 'long', year: 'numeric' })}
        </p>
        <div className="grid grid-cols-7 gap-3 text-center text-sm text-slate-400 mb-3">
          {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((d) => <div key={d}>{d}</div>)}
        </div>
        <div className="grid grid-cols-7 gap-3">
          {Array.from({ length: firstWeekday }).map((_, i) => <div key={`pad-${i}`} />)}
          {Array.from({ length: daysInMonth }).map((_, i) => {
            const day = i + 1
            const dateStr = new Date(month.getFullYear(), month.getMonth(), day).toISOString().slice(0, 10)
            const record = recordByDate.get(dateStr)
            const style = record ? STATUS_STYLES[record.status] : 'text-slate-400 hover:bg-slate-50'
            const ring = record ? APPROVAL_RING[record.approvalStatus] : ''
            return (
              <button
                key={day}
                onClick={() => record && setSelected(record)}
                className={`aspect-square rounded-xl text-base font-medium flex items-center justify-center ${style} ${ring}`}
              >
                {day}
              </button>
            )
          })}
        </div>
        <div className="flex flex-wrap gap-4 mt-5 text-xs text-slate-500">
          <Legend color="bg-emerald-500" label="Present" />
          <Legend color="bg-red-100" label="Absent" />
          <Legend color="bg-blue-100" label="Leave" />
          <Legend color="bg-slate-200" label="Holiday" />
          <Legend color="bg-white ring-2 ring-amber-400" label="Pending approval" />
          <Legend color="bg-white ring-2 ring-red-500" label="Rejected" />
        </div>
      </div>

      {selected && (
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm max-w-3xl">
          <p className="font-medium text-slate-800 mb-2">Details for {selected.date}</p>
          <dl className="text-sm text-slate-600 space-y-1">
            <div><dt className="inline font-medium">Status: </dt><dd className="inline">{selected.status}</dd></div>
            <div><dt className="inline font-medium">Approval: </dt><dd className="inline">{selected.approvalStatus}</dd></div>
            {selected.checkIn && <div><dt className="inline font-medium">Check-in: </dt><dd className="inline">{selected.checkIn}</dd></div>}
            {selected.checkOut && <div><dt className="inline font-medium">Check-out: </dt><dd className="inline">{selected.checkOut}</dd></div>}
            {selected.remarks && <div><dt className="inline font-medium">Remarks: </dt><dd className="inline">{selected.remarks}</dd></div>}
          </dl>
        </div>
      )}

      {showMark && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-sm w-full p-6">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Mark My Attendance</h3>
            <form onSubmit={handleMark} className="space-y-3">
              <input type="date" required value={form.date} max={new Date().toISOString().slice(0, 10)}
                onChange={(e) => setForm((f) => ({ ...f, date: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
              <select value={form.status} onChange={(e) => setForm((f) => ({ ...f, status: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm">
                <option value="PRESENT">Present</option>
                <option value="ABSENT">Absent</option>
                <option value="LEAVE">Leave</option>
              </select>
              {form.status === 'PRESENT' && (
                <div className="grid grid-cols-2 gap-3">
                  <input type="time" value={form.checkIn} onChange={(e) => setForm((f) => ({ ...f, checkIn: e.target.value }))}
                    className="rounded-lg border border-slate-300 px-3 py-2 text-sm" placeholder="Check-in" />
                  <input type="time" value={form.checkOut} onChange={(e) => setForm((f) => ({ ...f, checkOut: e.target.value }))}
                    className="rounded-lg border border-slate-300 px-3 py-2 text-sm" placeholder="Check-out" />
                </div>
              )}
              <input placeholder="Remarks (optional)" value={form.remarks}
                onChange={(e) => setForm((f) => ({ ...f, remarks: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
              <p className="text-xs text-slate-400">Your mentor will need to approve this before it counts toward your attendance.</p>
              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={() => setShowMark(false)} className="text-sm px-4 py-2 rounded-lg border border-slate-300 text-slate-600">
                  Cancel
                </button>
                <button type="submit" className="bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium">
                  Submit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}

function Legend({ color, label }: { color: string; label: string }) {
  return (
    <div className="flex items-center gap-1.5">
      <span className={`w-3 h-3 rounded ${color}`} />
      {label}
    </div>
  )
}
