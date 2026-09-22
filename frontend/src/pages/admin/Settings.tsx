import { useState } from 'react'
import { api, getApiErrorMessage } from '../../api/client'
import Toast from '../../components/shared/Toast'

export default function Settings() {
  const [form, setForm] = useState({ email: '', password: '', confirmPassword: '' })
  const [toast, setToast] = useState<string | null>(null)

  async function handleCreateAdmin(e: React.FormEvent) {
    e.preventDefault()
    try {
      await api.post('/auth/admins', form)
      setToast(`Admin account created for ${form.email}.`)
      setForm({ email: '', password: '', confirmPassword: '' })
    } catch (err) {
      setToast(getApiErrorMessage(err, 'Unable to create admin account.'))
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Settings</h1>
        <p className="text-sm text-slate-500">System configuration.</p>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm max-w-md">
        <h2 className="text-sm font-semibold text-slate-800 mb-1">Add Admin</h2>
        <p className="text-xs text-slate-500 mb-4">Grants full administrator access. Only add people you trust.</p>
        <form onSubmit={handleCreateAdmin} className="space-y-3">
          <input required type="email" placeholder="Email" value={form.email}
            onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
          <input required type="password" placeholder="Password (min. 8 characters)" value={form.password}
            onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
          <input required type="password" placeholder="Confirm password" value={form.confirmPassword}
            onChange={(e) => setForm((f) => ({ ...f, confirmPassword: e.target.value }))}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm" />
          <button type="submit" className="bg-brand-600 hover:bg-brand-700 text-white text-sm rounded-lg px-4 py-2 font-medium w-full">
            Create Admin Account
          </button>
        </form>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
        <p className="text-sm text-slate-500">
          Other admin-level settings (branding, notification preferences, system defaults) aren't implemented
          in this MVP yet — this section is a placeholder for that future work.
        </p>
      </div>

      {toast && <Toast message={toast} onClose={() => setToast(null)} />}
    </div>
  )
}
