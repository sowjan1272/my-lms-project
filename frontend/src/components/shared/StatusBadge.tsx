import { cn } from '../../lib/utils'

const STATUS_COLORS: Record<string, string> = {
  ACTIVE: 'bg-emerald-100 text-emerald-700',
  PRESENT: 'bg-emerald-100 text-emerald-700',
  PAID: 'bg-emerald-100 text-emerald-700',
  COMPLETED: 'bg-emerald-100 text-emerald-700',
  PENDING: 'bg-amber-100 text-amber-700',
  PROCESSING: 'bg-amber-100 text-amber-700',
  PARTIALLY_PAID: 'bg-amber-100 text-amber-700',
  IN_PROGRESS: 'bg-blue-100 text-blue-700',
  SUBMITTED: 'bg-blue-100 text-blue-700',
  LEAVE: 'bg-blue-100 text-blue-700',
  ABSENT: 'bg-red-100 text-red-700',
  OVERDUE: 'bg-red-100 text-red-700',
  FAILED: 'bg-red-100 text-red-700',
  SUSPENDED: 'bg-red-100 text-red-700',
  DROPPED: 'bg-red-100 text-red-700',
  NEEDS_REVISION: 'bg-orange-100 text-orange-700',
  HOLIDAY: 'bg-slate-200 text-slate-700',
  INACTIVE: 'bg-slate-200 text-slate-700',
  PLANNING: 'bg-slate-200 text-slate-700',
  ON_HOLD: 'bg-slate-200 text-slate-700',
  ARCHIVED: 'bg-slate-200 text-slate-700',
}

export default function StatusBadge({ status }: { status: string }) {
  const classes = STATUS_COLORS[status] || 'bg-slate-100 text-slate-700'
  return (
    <span className={cn('inline-block px-2.5 py-0.5 rounded-full text-xs font-medium', classes)}>
      {status.replace(/_/g, ' ')}
    </span>
  )
}
