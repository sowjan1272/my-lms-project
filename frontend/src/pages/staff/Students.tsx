import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import type { StudentSummary, PageResponse } from '../../types'

export default function Students() {
  const [students, setStudents] = useState<StudentSummary[]>([])
  const navigate = useNavigate()

  useEffect(() => {
    api.get<PageResponse<StudentSummary>>('/students/mine', { params: { size: 100 } })
      .then((res) => setStudents(res.data.content)).catch(() => {})
  }, [])

  const columns: Column<StudentSummary>[] = [
    {
      header: 'Student', accessor: (s) => (
        <div className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center text-xs font-semibold">
            {s.fullName.charAt(0)}
          </div>
          <div>
            <p className="font-medium text-slate-800">{s.fullName}</p>
            <p className="text-xs text-slate-400">{s.studentCode}</p>
          </div>
        </div>
      ),
    },
    { header: 'Attendance', accessor: (s) => `${s.attendancePct}%` },
    { header: 'Task Completion', accessor: (s) => `${s.taskCompletionPct}%` },
    { header: 'Status', accessor: (s) => <StatusBadge status={s.status} /> },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">My Students</h1>
        <p className="text-sm text-slate-500">Students currently assigned to you.</p>
      </div>

      <DataTable
        columns={columns}
        data={students}
        rowKey={(s) => s.id}
        onRowClick={(s) => navigate(`/staff/students/${s.id}`)}
        emptyMessage="No students assigned yet."
      />
    </div>
  )
}
