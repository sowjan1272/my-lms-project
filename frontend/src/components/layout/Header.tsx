import { useEffect, useRef, useState } from 'react'
import { Bell, LogOut, ChevronDown } from 'lucide-react'
import { useAuth } from '../../contexts/AuthContext'
import { api } from '../../api/client'
import { useNavigate } from 'react-router-dom'
import type { NotificationItem } from '../../types'

function timeAgo(dateStr: string): string {
  const diffMs = Date.now() - new Date(dateStr).getTime()
  const mins = Math.floor(diffMs / 60000)
  if (mins < 1) return 'just now'
  if (mins < 60) return `${mins}m ago`
  const hrs = Math.floor(mins / 60)
  if (hrs < 24) return `${hrs}h ago`
  return `${Math.floor(hrs / 24)}d ago`
}

export default function Header() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [unread, setUnread] = useState(0)
  const [menuOpen, setMenuOpen] = useState(false)
  const [notifOpen, setNotifOpen] = useState(false)
  const [notifications, setNotifications] = useState<NotificationItem[]>([])
  const [loadingNotifs, setLoadingNotifs] = useState(false)
  const notifRef = useRef<HTMLDivElement>(null)

  function loadUnreadCount() {
    if (!user) return
    api.get('/notifications/unread-count').then((res) => setUnread(res.data)).catch(() => {})
  }

  useEffect(loadUnreadCount, [user])

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (notifRef.current && !notifRef.current.contains(e.target as Node)) {
        setNotifOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  async function openNotifications() {
    const next = !notifOpen
    setNotifOpen(next)
    setMenuOpen(false)
    if (next) {
      setLoadingNotifs(true)
      try {
        const res = await api.get('/notifications', { params: { page: 0, size: 20 } })
        setNotifications(res.data.content)
      } catch {
        setNotifications([])
      } finally {
        setLoadingNotifs(false)
      }
    }
  }

  async function handleNotifClick(n: NotificationItem) {
    if (!n.read) {
      try {
        await api.patch(`/notifications/${n.id}/read`)
        setNotifications((prev) => prev.map((x) => (x.id === n.id ? { ...x, read: true } : x)))
        setUnread((c) => Math.max(0, c - 1))
      } catch {
        // ignore
      }
    }
  }

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <header className="h-16 border-b border-slate-200 bg-white flex items-center justify-end md:justify-between px-6 sticky top-0 z-30">
      <div className="hidden md:block">
        <p className="text-sm text-slate-400">Welcome back,</p>
        <p className="font-semibold text-slate-800 text-sm">{user?.fullName}</p>
      </div>

      <div className="flex items-center gap-4">
        <div className="relative" ref={notifRef}>
          <button onClick={openNotifications} className="relative p-2 rounded-lg hover:bg-slate-50 text-slate-500" aria-label="Notifications">
            <Bell size={20} />
            {unread > 0 && (
              <span className="absolute -top-0.5 -right-0.5 bg-red-500 text-white text-[10px] rounded-full w-4 h-4 flex items-center justify-center">
                {unread > 9 ? '9+' : unread}
              </span>
            )}
          </button>

          {notifOpen && (
            <div className="absolute right-0 mt-2 w-80 bg-white border border-slate-200 rounded-lg shadow-lg max-h-96 overflow-y-auto z-40">
              <div className="px-4 py-2.5 border-b border-slate-100">
                <p className="text-sm font-semibold text-slate-800">Notifications</p>
              </div>
              {loadingNotifs ? (
                <p className="text-sm text-slate-400 px-4 py-6 text-center">Loading...</p>
              ) : notifications.length === 0 ? (
                <p className="text-sm text-slate-400 px-4 py-6 text-center">No notifications yet.</p>
              ) : (
                <div className="divide-y divide-slate-100">
                  {notifications.map((n) => (
                    <button
                      key={n.id}
                      onClick={() => handleNotifClick(n)}
                      className={`w-full text-left px-4 py-3 hover:bg-slate-50 ${!n.read ? 'bg-brand-50/50' : ''}`}
                    >
                      <div className="flex items-start justify-between gap-2">
                        <p className="text-sm font-medium text-slate-800">{n.title}</p>
                        {!n.read && <span className="w-2 h-2 rounded-full bg-brand-600 mt-1.5 shrink-0" />}
                      </div>
                      <p className="text-xs text-slate-500 mt-0.5">{n.body}</p>
                      <p className="text-[11px] text-slate-400 mt-1">{timeAgo(n.createdAt)}</p>
                    </button>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>

        <div className="relative">
          <button
            onClick={() => { setMenuOpen((v) => !v); setNotifOpen(false) }}
            className="flex items-center gap-2 px-2 py-1.5 rounded-lg hover:bg-slate-50"
          >
            <div className="w-8 h-8 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center text-sm font-semibold">
              {user?.fullName?.charAt(0) || '?'}
            </div>
            <ChevronDown size={16} className="text-slate-400 hidden sm:block" />
          </button>

          {menuOpen && (
            <div className="absolute right-0 mt-2 w-44 bg-white border border-slate-200 rounded-lg shadow-lg py-1">
              <button
                onClick={handleLogout}
                className="w-full flex items-center gap-2 px-3 py-2 text-sm text-slate-600 hover:bg-slate-50"
              >
                <LogOut size={16} /> Log out
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}
