import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api, getApiErrorMessage } from '../../api/client'
import StatusBadge from '../../components/shared/StatusBadge'
import Toast from '../../components/shared/Toast'
import { formatCurrency, formatDate } from '../../lib/utils'
import type { StudentDetail as StudentDetailType, Payment, FeeSummary, Task, Enrollment, Course } from '../../types'

const TABS = ['Personal', 'Enrollments', 'Payments', 'Tasks'] as const
type Tab = typeof TABS[number]

export default function StudentDetail() {
  const { id } = useParams()
  const [student, setStudent] = useState<StudentDetailType | null>(null)
  const [payments, setPayments] = useState<Payment[]>([])
  const [feeSummary, setFeeSummary] = useState<FeeSummary | null>(null)
  const [enrollments, setEnrollments] = useState<Enrollment[]>([])
  const [courses, setCourses] = useState<Course[]>([])
  const [tab, setTab] = useState<Tab>('Personal')
  const [toast, setToast] = useState<string | null>(null)
  const [statusValue, setStatusValue] = useState('')
  const [showEnroll, setShowEnroll] = useState(false)
  const [enrollForm, setEnrollForm] = useState({ courseId: '', startDate: '', totalFee: '' })

  function loadEnrollments() {
    if (!id) return
    api.get(`/enrollments/student/${id}`).then((res) => setEnrollments(res.data)).catch(() => {})
  }

  useEffect(() => {
    if (!id) return
    api.get(`/students/${id}`).then((res) => { setStudent(res.data); setStatusValue(res.data.status) }).catch(() => {})
    api.get(`/payments/student/${id}`).then((res) => setPayments(res.data)).catch(() => {})
    api.get(`/payments/student/${id}/summary`).then((res) => setFeeSummary(res.data)).catch(() => {})
    api.get('/courses').then((res) => setCourses(res.data)).catch(() => {})
    loadEnrollments()
  }, [id])

  async function handleEnroll(e: React.FormEvent) {
    e.preventDefault()
    if (!id) return
    try {
      await api.post('/enrollments', {
        studentId: Number(id),
        courseId: Number(enrollForm.courseId),
        startDate: enrollForm.startDate || undefined,
        totalFee: enrollForm.totalFee ? Number(enrollForm.totalFee) : undefined,
      })
      setToast('Student enrolled successfully.')
      setShowEnroll(false)
      setEnrollForm({ courseId: '', startDate: '', totalFee: '' })
      loadEnrollments()
      api.get(`/payments/student/${id}/summary`).then((res) => setFeeSummary(res.data)).catch(() => {})
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to enroll student.'))
    }
  }

  async function handleStatusChange(newStatus: string) {
    if (!id) return
    try {
      await api.patch(`/students/${id}/status`, { status: newStatus })
      setStatusValue(newStatus)
      setToast('Student status updated.')
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to update status.'))
    }
  }

  if (!student) return <p className="text-sm text-slate-500">Loading student...</p>

  return (
    <div className="space-y-6 max-w-3xl">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center text-xl font-semibold">
            {student.fullName.charAt(0)}
          </div>
          <div>
            <h1 className="text-xl font-semibold text-slate-900">{student.fullName}</h1>
            <p className="text-sm text-slate-500">{student.studentCode}</p>
            <div className="mt-1"><StatusBadge status={statusValue} /></div>
          </div>
        </div>
        <select
          value={statusValue}
          onChange={(e) => handleStatusChange(e.target.value)}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
        >
          {['ACTIVE', 'COMPLETED', 'SUSPENDED', 'DROPPED', 'PENDING'].map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
      </div>

      <div className="border-b border-slate-200 flex gap-6">
        {TABS.map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`pb-2 text-sm font-medium border-b-2 ${tab === t ? 'border-brand-600 text-brand-700' : 'border-transparent text-slate-500'}`}
          >
            {t}
          </button>
        ))}
      </div>

      {tab === 'Personal' && (
        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
          <dl className="grid sm:grid-cols-2 gap-4 text-sm">
            <Detail label="Email" value={student.email} />
            <Detail label="Phone" value={student.phone} />
            <Detail label="Date of Birth" value={formatDate(student.dob)} />
            <Detail label="Gender" value={student.gender || '—'} />
            <Detail label="Qualification" value={student.qualification || '—'} />
            <Detail label="College" value={student.college || '—'} />
            <Detail label="Mentor" value={student.mentorName || 'Not assigned'} />
            <Detail label="Registered On" value={formatDate(student.createdAt)} />
          </dl>
        </div>
      )}

      {tab === 'Enrollments' && (
        <div className="space-y-4">
          <div className="flex justify-end">
            <button onClick={() => setShowEnroll(true)} className="bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium">
              + Enroll in Course
            </button>
          </div>
          {enrollments.length === 0 ? (
            <p className="text-sm text-slate-400">Not enrolled in any course yet.</p>
          ) : (
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm divide-y divide-slate-100">
              {enrollments.map((en) => (
                <div key={en.id} className="p-4 flex items-center justify-between text-sm">
                  <div>
                    <p className="font-medium text-slate-800">{en.courseName}</p>
                    <p className="text-xs text-slate-500">{formatDate(en.startDate)} → {formatDate(en.endDate)} · {en.progressPct}% complete</p>
                  </div>
                  <span className="font-medium">{formatCurrency(en.totalFee)}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {showEnroll && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Enroll in Course / Internship</h3>
            <form onSubmit={handleEnroll} className="space-y-3">
              <select required value={enrollForm.courseId}
                onChange={(e) => setEnrollForm((f) => ({ ...f, courseId: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm">
                <option value="">Select a course...</option>
                {courses.filter((c) => c.active).map((c) => (
                  <option key={c.id} value={c.id}>{c.name} ({c.type}) — {formatCurrency(c.fee)}</option>
                ))}
              </select>
              <div>
                <label className="text-xs text-slate-500">Start date (optional, defaults to today)</label>
                <input type="date" value={enrollForm.startDate}
                  onChange={(e) => setEnrollForm((f) => ({ ...f, startDate: e.target.value }))}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm mt-1" />
              </div>
              <div>
                <label className="text-xs text-slate-500">Total fee (optional, defaults to course fee)</label>
                <input type="number" min="0" step="0.01" placeholder="e.g. 30000" value={enrollForm.totalFee}
                  onChange={(e) => setEnrollForm((f) => ({ ...f, totalFee: e.target.value }))}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm mt-1" />
              </div>
              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={() => setShowEnroll(false)} className="text-sm px-4 py-2 rounded-lg border border-slate-300 text-slate-600">
                  Cancel
                </button>
                <button type="submit" className="bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium">
                  Enroll
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {tab === 'Payments' && (
        <div className="space-y-4">
          {feeSummary && (
            <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm grid sm:grid-cols-3 gap-4">
              <Detail label="Total Fee" value={formatCurrency(feeSummary.totalFee)} />
              <Detail label="Paid" value={formatCurrency(feeSummary.amountPaid)} />
              <Detail label="Remaining" value={formatCurrency(feeSummary.remaining)} />
            </div>
          )}
          {payments.length === 0 ? (
            <p className="text-sm text-slate-400">No payment history available.</p>
          ) : (
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm divide-y divide-slate-100">
              {payments.map((p) => (
                <div key={p.id} className="p-4 flex justify-between text-sm">
                  <span>{formatDate(p.paidAt)} · {p.method}</span>
                  <span className="font-medium">{formatCurrency(p.amount)}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {tab === 'Tasks' && <StudentTasksTab studentId={id!} />}

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}

function StudentTasksTab({ studentId }: { studentId: string }) {
  const [tasks, setTasks] = useState<Task[]>([])
  useEffect(() => {
    // No dedicated admin-by-student tasks endpoint in this MVP; students' own list endpoint
    // is student-scoped, so this tab is best-effort and may show empty for admin view.
    setTasks([])
  }, [studentId])

  if (tasks.length === 0) return <p className="text-sm text-slate-400">No task data available in this view yet.</p>
  return null
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-xs text-slate-500">{label}</dt>
      <dd className="font-medium text-slate-800">{value}</dd>
    </div>
  )
}
