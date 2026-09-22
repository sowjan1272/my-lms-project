import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import { formatDate } from '../../lib/utils'
import type { Task } from '../../types'

export default function Tasks() {
  const [tasks, setTasks] = useState<Task[]>([])

  useEffect(() => {
    api.get('/tasks').then((res) => setTasks(res.data)).catch(() => {})
  }, [])

  const columns: Column<Task>[] = [
    {
      header: 'Task', accessor: (t) => (
        <div>
          <p className="font-medium text-slate-800">{t.title}</p>
          {t.category && <p className="text-xs text-slate-400">{t.category}</p>}
        </div>
      ),
    },
    { header: 'Student', accessor: (t) => t.studentName },
    { header: 'Assigned By', accessor: (t) => t.assignedByName },
    { header: 'Priority', accessor: (t) => <StatusBadge status={t.priority} /> },
    { header: 'Due Date', accessor: (t) => formatDate(t.dueDate) },
    { header: 'Status', accessor: (t) => <StatusBadge status={t.status} /> },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Tasks</h1>
        <p className="text-sm text-slate-500">System-wide view of tasks assigned by staff to students.</p>
      </div>

      <DataTable columns={columns} data={tasks} rowKey={(t) => t.id} emptyMessage="No tasks found." />
    </div>
  )
}
