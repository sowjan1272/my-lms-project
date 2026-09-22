import { useEffect, useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import Toast from '../../components/shared/Toast'
import { formatDate } from '../../lib/utils'
import type { Task, TaskSubmission, StudentSummary, PageResponse } from '../../types'

export default function Tasks() {
  const [tasks, setTasks] = useState<Task[]>([])
  const [students, setStudents] = useState<StudentSummary[]>([])
  const [showCreate, setShowCreate] = useState(false)
  const [reviewTask, setReviewTask] = useState<Task | null>(null)
  const [submissions, setSubmissions] = useState<TaskSubmission[]>([])
  const [toast, setToast] = useState<string | null>(null)

  const [form, setForm] = useState({ title: '', description: '', dueDate: '', priority: 'MEDIUM', studentIds: [] as number[] })

  function load() {
    api.get('/tasks/staff/me').then((res) => setTasks(res.data)).catch(() => {})
  }

  useEffect(() => {
    load()
    api.get<PageResponse<StudentSummary>>('/students/mine', { params: { size: 100 } })
      .then((res) => setStudents(res.data.content)).catch(() => {})
  }, [])

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    if (form.studentIds.length === 0) {
      setToast('Select at least one student.')
      return
    }
    try {
      await api.post('/tasks', form)
      setToast('Task created and assigned.')
      setShowCreate(false)
      setForm({ title: '', description: '', dueDate: '', priority: 'MEDIUM', studentIds: [] })
      load()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to create task.'))
    }
  }

  async function openReview(task: Task) {
    setReviewTask(task)
    const res = await api.get(`/tasks/${task.id}/submissions`)
    setSubmissions(res.data)
  }

  async function handleReview(decision: 'COMPLETED' | 'NEEDS_REVISION', feedback: string) {
    if (!reviewTask || submissions.length === 0) return
    try {
      await api.patch(`/tasks/${reviewTask.id}/submissions/${submissions[0].id}`, { decision, feedback })
      setToast('Review submitted.')
      setReviewTask(null)
      load()
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to submit review.'))
    }
  }

  const columns: Column<Task>[] = [
    { header: 'Title', accessor: (t) => t.title },
    { header: 'Student', accessor: (t) => t.studentName },
    { header: 'Priority', accessor: (t) => t.priority },
    { header: 'Due', accessor: (t) => formatDate(t.dueDate) },
    { header: 'Status', accessor: (t) => <StatusBadge status={t.status} /> },
    {
      header: '', accessor: (t) => (
        t.status === 'SUBMITTED' ? (
          <button onClick={() => openReview(t)} className="text-brand-600 text-xs font-medium hover:underline">Review</button>
        ) : null
      ),
    },
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-900">Tasks</h1>
          <p className="text-sm text-slate-500">Tasks you've assigned to your students.</p>
        </div>
        <button onClick={() => setShowCreate(true)} className="bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium">
          + New Task
        </button>
      </div>

      <DataTable columns={columns} data={tasks} rowKey={(t) => t.id} emptyMessage="No tasks created yet." />

      {showCreate && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Create Task</h3>
            <form onSubmit={handleCreate} className="space-y-3">
              <input required placeholder="Task title" value={form.title}
                onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
              <textarea required placeholder="Description" value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm h-20" />
              <div className="grid grid-cols-2 gap-3">
                <input required type="date" value={form.dueDate}
                  onChange={(e) => setForm((f) => ({ ...f, dueDate: e.target.value }))}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
                <select value={form.priority} onChange={(e) => setForm((f) => ({ ...f, priority: e.target.value }))}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm">
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                  <option value="URGENT">Urgent</option>
                </select>
              </div>
              <div>
                <p className="text-sm font-medium text-slate-700 mb-1">Assign to</p>
                <div className="max-h-32 overflow-y-auto border border-slate-200 rounded-lg p-2 space-y-1">
                  {students.map((s) => (
                    <label key={s.id} className="flex items-center gap-2 text-sm">
                      <input
                        type="checkbox"
                        checked={form.studentIds.includes(s.id)}
                        onChange={(e) => {
                          setForm((f) => ({
                            ...f,
                            studentIds: e.target.checked
                              ? [...f.studentIds, s.id]
                              : f.studentIds.filter((id) => id !== s.id),
                          }))
                        }}
                      />
                      {s.fullName}
                    </label>
                  ))}
                </div>
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => setShowCreate(false)} className="px-4 py-2 text-sm rounded-lg border border-slate-300">
                  Cancel
                </button>
                <button type="submit" className="px-4 py-2 text-sm rounded-lg bg-brand-600 text-white">Create</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {reviewTask && (
        <ReviewModal
          task={reviewTask}
          submission={submissions[0]}
          onClose={() => setReviewTask(null)}
          onReview={handleReview}
        />
      )}

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}

function ReviewModal({ task, submission, onClose, onReview }: {
  task: Task; submission?: TaskSubmission; onClose: () => void
  onReview: (decision: 'COMPLETED' | 'NEEDS_REVISION', feedback: string) => void
}) {
  const [feedback, setFeedback] = useState('')
  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6">
        <h3 className="text-lg font-semibold text-slate-900 mb-3">Review: {task.title}</h3>
        {submission ? (
          <div className="text-sm text-slate-600 space-y-2 mb-4">
            {submission.textResponse && <p>{submission.textResponse}</p>}
            {submission.githubLink && <p><a href={submission.githubLink} target="_blank" rel="noreferrer" className="text-brand-600 hover:underline">GitHub link</a></p>}
            {submission.demoLink && <p><a href={submission.demoLink} target="_blank" rel="noreferrer" className="text-brand-600 hover:underline">Demo link</a></p>}
          </div>
        ) : (
          <p className="text-sm text-slate-400 mb-4">No submission found.</p>
        )}
        <textarea
          placeholder="Feedback for the student"
          value={feedback}
          onChange={(e) => setFeedback(e.target.value)}
          className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm h-20 mb-4"
        />
        <div className="flex justify-end gap-3">
          <button onClick={onClose} className="px-4 py-2 text-sm rounded-lg border border-slate-300">Cancel</button>
          <button onClick={() => onReview('NEEDS_REVISION', feedback)} className="px-4 py-2 text-sm rounded-lg border border-amber-300 text-amber-700">
            Request Revision
          </button>
          <button onClick={() => onReview('COMPLETED', feedback)} className="px-4 py-2 text-sm rounded-lg bg-emerald-600 text-white">
            Mark Completed
          </button>
        </div>
      </div>
    </div>
  )
}
