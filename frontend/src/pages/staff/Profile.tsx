import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import type { StaffDetail } from '../../types'

export default function Profile() {
  const [profile, setProfile] = useState<StaffDetail | null>(null)

  useEffect(() => {
    api.get('/staff/me').then((res) => setProfile(res.data)).catch(() => {})
  }, [])

  if (!profile) return <p className="text-sm text-slate-500">Loading profile...</p>

  return (
    <div className="space-y-6 max-w-2xl">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">My Profile</h1>
        <p className="text-sm text-slate-500">Your staff details.</p>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-16 h-16 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center text-xl font-semibold">
            {profile.fullName.charAt(0)}
          </div>
          <div>
            <p className="font-semibold text-slate-900 text-lg">{profile.fullName}</p>
            <p className="text-sm text-slate-500">{profile.staffCode}</p>
          </div>
        </div>

        <dl className="grid sm:grid-cols-2 gap-4 text-sm">
          <Detail label="Email" value={profile.email} />
          <Detail label="Phone" value={profile.phone} />
          <Detail label="Designation" value={profile.designation} />
          <Detail label="Department" value={profile.department} />
          <Detail label="Qualification" value={profile.qualification || '—'} />
          <Detail label="Skills" value={profile.skills.join(', ') || '—'} />
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
