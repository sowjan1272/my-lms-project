import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../../api/client'
import StatusBadge from '../../components/shared/StatusBadge'
import { formatDate } from '../../lib/utils'
import type { StudentDetail as StudentDetailType } from '../../types'

export default function StudentDetail() {
  const { id } = useParams()
  const [student, setStudent] = useState<StudentDetailType | null>(null)

  useEffect(() => {
    if (id) api.get(`/students/${id}`).then((res) => setStudent(res.data)).catch(() => {})
  }, [id])

  if (!student) return <p className="text-sm text-slate-500">Loading student...</p>

  return (
    <div className="space-y-6 max-w-2xl">
      <div className="flex items-center gap-4">
        <div className="w-16 h-16 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center text-xl font-semibold">
          {student.fullName.charAt(0)}
        </div>
        <div>
          <h1 className="text-xl font-semibold text-slate-900">{student.fullName}</h1>
          <p className="text-sm text-slate-500">{student.studentCode}</p>
          <div className="mt-1"><StatusBadge status={student.status} /></div>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
        <dl className="grid sm:grid-cols-2 gap-4 text-sm">
          <Detail label="Email" value={student.email} />
          <Detail label="Phone" value={student.phone} />
          <Detail label="Qualification" value={student.qualification || '—'} />
          <Detail label="College" value={student.college || '—'} />
          <Detail label="Registered On" value={formatDate(student.createdAt)} />
        </dl>
      </div>
    </div>
  )
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-xs text-slate-500">{label}</dt>
      <dd className="font-medium text-slate-800">{value}</dd>
    </div>
  )
}
