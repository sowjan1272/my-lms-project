import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../../api/client'
import DataTable, { Column } from '../../components/shared/DataTable'
import StatusBadge from '../../components/shared/StatusBadge'
import type { StudentSummary, PageResponse } from '../../types'

export default function Students() {
  const [students, setStudents] = useState<StudentSummary[]>([])
  const [search, setSearch] = useState('')
  const navigate = useNavigate()

  function load(q: string) {
    api.get<PageResponse<StudentSummary>>('/students', { params: { search: q, size: 100 } })
      .then((res) => setStudents(res.data.content)).catch(() => {})
  }

  useEffect(() => load(''), [])

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
    { header: 'Email', accessor: (s) => s.email },
    { header: 'Phone', accessor: (s) => s.phone },
    { header: 'Mentor', accessor: (s) => s.mentorName || '—' },
    { header: 'Attendance', accessor: (s) => `${s.attendancePct}%` },
    { header: 'Status', accessor: (s) => <StatusBadge status={s.status} /> },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Students</h1>
        <p className="text-sm text-slate-500">Manage all registered students.</p>
      </div>

      <DataTable
        columns={columns}
        data={students}
        rowKey={(s) => s.id}
        searchable
        onSearch={(q) => { setSearch(q); load(q) }}
        onRowClick={(s) => navigate(`/admin/students/${s.id}`)}
        emptyMessage="No students found."
      />
    </div>
  )
}
