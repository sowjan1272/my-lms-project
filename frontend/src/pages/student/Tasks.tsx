import { useEffect, useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import StatusBadge from '../../components/shared/StatusBadge'
import EmptyState from '../../components/shared/EmptyState'
import Toast from '../../components/shared/Toast'
import { formatDate } from '../../lib/utils'
import type { Task } from '../../types'

export default function Tasks() {
  const [tasks, setTasks] = useState<Task[]>([])
  const [active, setActive] = useState<Task | null>(null)
  const [form, setForm] = useState({ textResponse: '', fileUrl: '', githubLink: '', demoLink: '' })
  const [toast, setToast] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  function load() {
    api.get('/tasks/student/me').then((res) => setTasks(res.data)).catch(() => {})
  }

  useEffect(load, [])

  async function handleSubmitTask(e: React.FormEvent) {
    e.preventDefault()
    if (!active) return
    setSubmitting(true)
    try {
      await api.post(`/tasks/${active.id}/submissions`, form)
      setToast('Task submitted successfully.')
      setActive(null)
      setForm({ textResponse: '', fileUrl: '', githubLink: '', demoLink: '' })
      load()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to submit task.'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">My Tasks</h1>
        <p className="text-sm text-slate-500">Tasks assigned to you by your mentors.</p>
      </div>

      {tasks.length === 0 ? (
        <EmptyState message="No tasks assigned yet." />
      ) : (
        <div className="grid gap-4">
          {tasks.map((task) => (
            <div key={task.id} className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <h3 className="font-semibold text-slate-900">{task.title}</h3>
                    <StatusBadge status={task.status} />
                  </div>
                  <p className="text-sm text-slate-600">{task.description}</p>
                  <div className="flex items-center gap-2 mt-3 text-xs text-slate-500">
                    <div className="w-6 h-6 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center font-semibold">
                      {task.assignedByName.charAt(0)}
                    </div>
                    <span>{task.assignedByName}</span>
                    <span>·</span>
                    <span>Due {formatDate(task.dueDate)}</span>
                    <span>·</span>
                    <span>{task.priority} priority</span>
                  </div>
                </div>
                {(task.status === 'PENDING' || task.status === 'IN_PROGRESS' || task.status === 'NEEDS_REVISION' || task.status === 'OVERDUE') && (
                  <button
                    onClick={() => setActive(task)}
                    className="shrink-0 bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium"
                  >
                    Submit Task
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {active && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Submit: {active.title}</h3>
            <form onSubmit={handleSubmitTask} className="space-y-3">
              <textarea
                placeholder="Description / text response"
                value={form.textResponse}
                onChange={(e) => setForm((f) => ({ ...f, textResponse: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm h-24"
              />
              <input
                placeholder="File URL (optional)"
                value={form.fileUrl}
                onChange={(e) => setForm((f) => ({ ...f, fileUrl: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
              />
              <input
                placeholder="GitHub link (optional)"
                value={form.githubLink}
                onChange={(e) => setForm((f) => ({ ...f, githubLink: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
              />
              <input
                placeholder="Demo/project link (optional)"
                value={form.demoLink}
                onChange={(e) => setForm((f) => ({ ...f, demoLink: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
              />
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => setActive(null)} className="px-4 py-2 text-sm rounded-lg border border-slate-300">
                  Cancel
                </button>
                <button type="submit" disabled={submitting} className="px-4 py-2 text-sm rounded-lg bg-brand-600 text-white disabled:opacity-60">
                  {submitting ? 'Submitting...' : 'Submit'}
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
