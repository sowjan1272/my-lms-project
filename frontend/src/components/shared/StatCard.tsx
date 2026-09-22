import { LucideIcon } from 'lucide-react'

interface Props {
  label: string
  value: string | number
  icon?: LucideIcon
  accent?: 'blue' | 'green' | 'amber' | 'red' | 'slate'
}

const accentClasses: Record<string, string> = {
  blue: 'bg-blue-50 text-blue-600',
  green: 'bg-emerald-50 text-emerald-600',
  amber: 'bg-amber-50 text-amber-600',
  red: 'bg-red-50 text-red-600',
  slate: 'bg-slate-100 text-slate-600',
}

export default function StatCard({ label, value, icon: Icon, accent = 'blue' }: Props) {
  return (
    <div className="bg-white rounded-xl border border-slate-200 p-5 flex items-center justify-between shadow-sm">
      <div>
        <p className="text-sm text-slate-500 font-medium">{label}</p>
        <p className="text-2xl font-semibold text-slate-900 mt-1">{value}</p>
      </div>
      {Icon && (
        <div className={`w-11 h-11 rounded-lg flex items-center justify-center ${accentClasses[accent]}`}>
          <Icon size={22} />
        </div>
      )}
    </div>
  )
}
