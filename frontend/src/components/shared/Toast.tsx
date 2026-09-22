import { useEffect } from 'react'
import { CheckCircle2, XCircle } from 'lucide-react'

interface Props {
  message: string
  type?: 'success' | 'error'
  onClose: () => void
}

export default function Toast({ message, type = 'success', onClose }: Props) {
  useEffect(() => {
    const timer = setTimeout(onClose, 3500)
    return () => clearTimeout(timer)
  }, [onClose])

  const Icon = type === 'success' ? CheckCircle2 : XCircle
  const colors = type === 'success' ? 'bg-emerald-600' : 'bg-red-600'

  return (
    <div className={`fixed bottom-6 right-6 ${colors} text-white rounded-lg shadow-lg px-4 py-3 flex items-center gap-2 z-50`}>
      <Icon size={18} />
      <span className="text-sm">{message}</span>
    </div>
  )
}
