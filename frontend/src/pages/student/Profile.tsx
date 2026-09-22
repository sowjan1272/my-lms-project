import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import StatusBadge from '../../components/shared/StatusBadge'
import { formatDate } from '../../lib/utils'
import type { StudentDetail } from '../../types'

export default function Profile() {
  const [profile, setProfile] = useState<StudentDetail | null>(null)

  useEffect(() => {
    api.get('/students/me').then((res) => setProfile(res.data)).catch(() => {})
  }, [])

  if (!profile) return <p className="text-sm text-slate-500">Loading profile...</p>

  return (
    <div className="space-y-6 max-w-2xl">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">My Profile</h1>
        <p className="text-sm text-slate-500">Your personal and enrollment details.</p>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-16 h-16 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center text-xl font-semibold">
            {profile.fullName.charAt(0)}
          </div>
          <div>
            <p className="font-semibold text-slate-900 text-lg">{profile.fullName}</p>
            <p className="text-sm text-slate-500">{profile.studentCode}</p>
            <div className="mt-1"><StatusBadge status={profile.status} /></div>
          </div>
        </div>

        <dl className="grid sm:grid-cols-2 gap-4 text-sm">
          <Detail label="Email" value={profile.email} />
          <Detail label="Phone" value={profile.phone} />
          <Detail label="Date of Birth" value={formatDate(profile.dob)} />
          <Detail label="Gender" value={profile.gender || '—'} />
          <Detail label="Qualification" value={profile.qualification || '—'} />
          <Detail label="College" value={profile.college || '—'} />
          <Detail label="Emergency Contact" value={profile.emergencyContact || '—'} />
          <Detail label="Mentor" value={profile.mentorName || 'Not assigned'} />
          <Detail label="Address" value={profile.address || '—'} />
          <Detail label="Registered On" value={formatDate(profile.createdAt)} />
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
