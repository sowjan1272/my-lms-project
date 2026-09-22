import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import EmptyState from '../../components/shared/EmptyState'
import { formatCurrency, formatDate } from '../../lib/utils'
import type { Enrollment, FeeSummary } from '../../types'

export default function Course() {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([])
  const [feeSummary, setFeeSummary] = useState<FeeSummary | null>(null)

  useEffect(() => {
    api.get('/enrollments/student/me').then((res) => setEnrollments(res.data)).catch(() => {})
    api.get('/payments/student/me/summary').then((res) => setFeeSummary(res.data)).catch(() => {})
  }, [])

  if (enrollments.length === 0) {
    return <EmptyState message="You're not enrolled in any course or internship yet. Contact the admin team to get enrolled." />
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Course / Internship</h1>
        <p className="text-sm text-slate-500">Details of your enrolled program.</p>
      </div>

      {feeSummary && (
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm grid sm:grid-cols-3 gap-4">
          <div>
            <p className="text-xs text-slate-500">Total Fee</p>
            <p className="font-semibold text-slate-900">{formatCurrency(feeSummary.totalFee)}</p>
          </div>
          <div>
            <p className="text-xs text-slate-500">Paid</p>
            <p className="font-semibold text-emerald-600">{formatCurrency(feeSummary.amountPaid)}</p>
          </div>
          <div>
            <p className="text-xs text-slate-500">Remaining</p>
            <p className="font-semibold text-amber-600">{formatCurrency(feeSummary.remaining)}</p>
          </div>
        </div>
      )}

      <div className="grid gap-4">
        {enrollments.map((en) => (
          <div key={en.id} className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <div className="flex items-start justify-between">
              <div>
                <h3 className="font-semibold text-slate-900">{en.courseName}</h3>
                <p className="text-xs text-slate-500 uppercase tracking-wide mt-0.5">{en.courseType}</p>
              </div>
              <span className="text-sm font-medium text-slate-600">
                {formatDate(en.startDate)} → {formatDate(en.endDate)}
              </span>
            </div>
            <div className="mt-3">
              <div className="flex justify-between text-xs text-slate-500 mb-1">
                <span>Progress</span>
                <span>{en.progressPct}%</span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-2">
                <div className="bg-brand-600 h-2 rounded-full" style={{ width: `${en.progressPct}%` }} />
              </div>
            </div>
            <p className="text-sm text-slate-500 mt-3">Program fee: <span className="font-medium text-slate-700">{formatCurrency(en.totalFee)}</span></p>
          </div>
        ))}
      </div>
    </div>
  )
}
