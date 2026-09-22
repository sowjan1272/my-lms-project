import { useEffect, useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import Toast from '../../components/shared/Toast'
import { formatDate } from '../../lib/utils'
import type { ProjectItem, StaffSummary, StudentSummary, PageResponse } from '../../types'

export default function Projects() {
  const [projects, setProjects] = useState<ProjectItem[]>([])
  const [staffList, setStaffList] = useState<StaffSummary[]>([])
  const [students, setStudents] = useState<StudentSummary[]>([])
  const [showCreate, setShowCreate] = useState(false)
  const [toast, setToast] = useState<string | null>(null)
  const [form, setForm] = useState({
    name: '', description: '', technologies: '', startDate: '', staffIds: [] as number[], studentIds: [] as number[],
  })

  function load() {
    api.get('/projects').then((res) => setProjects(res.data)).catch(() => {})
  }

  useEffect(() => {
    load()
    api.get<PageResponse<StaffSummary>>('/staff', { params: { size: 200 } }).then((res) => setStaffList(res.data.content)).catch(() => {})
    api.get<PageResponse<StudentSummary>>('/students', { params: { size: 200 } }).then((res) => setStudents(res.data.content)).catch(() => {})
  }, [])

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    try {
      await api.post('/projects', form)
      setToast('Project created.')
      setShowCreate(false)
      setForm({ name: '', description: '', technologies: '', startDate: '', staffIds: [], studentIds: [] })
      load()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to create project.'))
    }
  }

  const columns: Column<ProjectItem>[] = [
    { header: 'Name', accessor: (p) => p.name },
    { header: 'Status', accessor: (p) => <StatusBadge status={p.status} /> },
    { header: 'Progress', accessor: (p) => `${p.progressPct}%` },
    { header: 'Started', accessor: (p) => formatDate(p.startDate) },
    { header: 'Team', accessor: (p) => p.memberNames.join(', ') || '—' },
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-900">Projects</h1>
          <p className="text-sm text-slate-500">Company-wide project tracking.</p>
        </div>
        <button onClick={() => setShowCreate(true)} className="bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium">
          + New Project
        </button>
      </div>

      <DataTable columns={columns} data={projects} rowKey={(p) => p.id} emptyMessage="No projects found." />

      {showCreate && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">New Project</h3>
            <form onSubmit={handleCreate} className="space-y-3">
              <input required placeholder="Project name" value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
              <textarea placeholder="Description" value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm h-20" />
              <input placeholder="Technologies (comma-separated)" value={form.technologies}
                onChange={(e) => setForm((f) => ({ ...f, technologies: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
              <input required type="date" value={form.startDate}
                onChange={(e) => setForm((f) => ({ ...f, startDate: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />

              <div>
                <p className="text-sm font-medium text-slate-700 mb-1">Staff members</p>
                <div className="max-h-24 overflow-y-auto border border-slate-200 rounded-lg p-2 space-y-1">
                  {staffList.map((s) => (
                    <label key={s.id} className="flex items-center gap-2 text-sm">
                      <input type="checkbox" checked={form.staffIds.includes(s.id)}
                        onChange={(e) => setForm((f) => ({
                          ...f, staffIds: e.target.checked ? [...f.staffIds, s.id] : f.staffIds.filter((id) => id !== s.id),
                        }))} />
                      {s.fullName}
                    </label>
                  ))}
                </div>
              </div>

              <div>
                <p className="text-sm font-medium text-slate-700 mb-1">Students</p>
                <div className="max-h-24 overflow-y-auto border border-slate-200 rounded-lg p-2 space-y-1">
                  {students.map((s) => (
                    <label key={s.id} className="flex items-center gap-2 text-sm">
                      <input type="checkbox" checked={form.studentIds.includes(s.id)}
                        onChange={(e) => setForm((f) => ({
                          ...f, studentIds: e.target.checked ? [...f.studentIds, s.id] : f.studentIds.filter((id) => id !== s.id),
                        }))} />
                      {s.fullName}
                    </label>
                  ))}
                </div>
              </div>

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
