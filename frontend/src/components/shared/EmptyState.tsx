import { Inbox } from 'lucide-react'

interface Props {
  message: string
  subtext?: string
}

export default function EmptyState({ message, subtext }: Props) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center text-slate-400">
      <Inbox size={40} className="mb-3" />
      <p className="text-slate-600 font-medium">{message}</p>
      {subtext && <p className="text-sm mt-1">{subtext}</p>}
    </div>
  )
}
