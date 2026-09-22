import { useEffect, useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import Toast from '../../components/shared/Toast'
import { formatCurrency } from '../../lib/utils'
import type { Course, StaffSummary, PageResponse } from '../../types'

export default function Courses() {
  const [courses, setCourses] = useState<Course[]>([])
  const [staff, setStaff] = useState<StaffSummary[]>([])
  const [showCreate, setShowCreate] = useState(false)
  const [toast, setToast] = useState<string | null>(null)
  const [form, setForm] = useState({ name: '', description: '', type: 'COURSE', durationMonths: '', fee: '', mentorId: '', modulesInput: '' })

  function load() {
    api.get('/courses').then((res) => setCourses(res.data)).catch(() => {})
  }
  useEffect(() => {
    load()
    api.get<PageResponse<StaffSummary>>('/staff', { params: { size: 100 } })
      .then((res) => setStaff(res.data.content)).catch(() => {})
  }, [])

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    try {
      const modules = form.modulesInput.split(',').map((m) => m.trim()).filter(Boolean)
      await api.post('/courses', {
        name: form.name, description: form.description, type: form.type,
        durationMonths: Number(form.durationMonths), fee: Number(form.fee),
        mentorId: form.mentorId ? Number(form.mentorId) : undefined, modules,
      })
      setToast('Course created.')
      setShowCreate(false)
      setForm({ name: '', description: '', type: 'COURSE', durationMonths: '', fee: '', mentorId: '', modulesInput: '' })
      load()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to create course.'))
    }
  }

  const columns: Column<Course>[] = [
    { header: 'Name', accessor: (c) => c.name },
    { header: 'Type', accessor: (c) => <StatusBadge status={c.type} /> },
    { header: 'Duration', accessor: (c) => `${c.durationMonths} mo` },
    { header: 'Fee', accessor: (c) => formatCurrency(c.fee) },
    { header: 'Mentor', accessor: (c) => c.mentorName || '—' },
    { header: 'Enrolled', accessor: (c) => c.enrolledCount },
    { header: 'Status', accessor: (c) => <StatusBadge status={c.active ? 'ACTIVE' : 'INACTIVE'} /> },
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-900">Courses & Internships</h1>
          <p className="text-sm text-slate-500">Manage the programs offered.</p>
        </div>
        <button onClick={() => setShowCreate(true)} className="bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium">
          + New Course
        </button>
      </div>

      <DataTable columns={columns} data={courses} rowKey={(c) => c.id} emptyMessage="No courses found." />

      {showCreate && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">New Course / Internship</h3>
            <form onSubmit={handleCreate} className="space-y-3">
              <input required placeholder="Name" value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
              <textarea placeholder="Description" value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm h-20" />
              <div className="grid grid-cols-3 gap-3">
                <select value={form.type} onChange={(e) => setForm((f) => ({ ...f, type: e.target.value }))}
                  className="rounded-lg border border-slate-300 px-3 py-2 text-sm">
                  <option value="COURSE">Course</option>
                  <option value="INTERNSHIP">Internship</option>
                </select>
                <input required type="number" placeholder="Months" value={form.durationMonths}
                  onChange={(e) => setForm((f) => ({ ...f, durationMonths: e.target.value }))}
                  className="rounded-lg border border-slate-300 px-3 py-2 text-sm" />
                <input required type="number" placeholder="Fee" value={form.fee}
                  onChange={(e) => setForm((f) => ({ ...f, fee: e.target.value }))}
                  className="rounded-lg border border-slate-300 px-3 py-2 text-sm" />
              </div>
              <select value={form.mentorId} onChange={(e) => setForm((f) => ({ ...f, mentorId: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm">
                <option value="">No mentor assigned yet</option>
                {staff.filter((s) => s.active).map((s) => (
                  <option key={s.id} value={s.id}>{s.fullName} — {s.designation}</option>
                ))}
              </select>
              <input placeholder="Modules (comma-separated)" value={form.modulesInput}
                onChange={(e) => setForm((f) => ({ ...f, modulesInput: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => setShowCreate(false)} className="px-4 py-2 text-sm rounded-lg border border-slate-300">Cancel</button>
                <button type="submit" className="px-4 py-2 text-sm rounded-lg bg-brand-600 text-white">Create</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}
