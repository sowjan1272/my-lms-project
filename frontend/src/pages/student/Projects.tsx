import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import EmptyState from '../../components/shared/EmptyState'
import StatusBadge from '../../components/shared/StatusBadge'
import { formatDate } from '../../lib/utils'
import type { ProjectItem } from '../../types'

export default function Projects() {
  const [projects, setProjects] = useState<ProjectItem[]>([])

  useEffect(() => {
    api.get('/projects/student/me').then((res) => setProjects(res.data)).catch(() => {})
  }, [])

  if (projects.length === 0) return <EmptyState message="No projects found." />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">My Projects</h1>
        <p className="text-sm text-slate-500">Projects you're currently part of.</p>
      </div>

      <div className="grid gap-4">
        {projects.map((p) => (
          <div key={p.id} className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <div className="flex items-start justify-between">
              <h3 className="font-semibold text-slate-900">{p.name}</h3>
              <StatusBadge status={p.status} />
            </div>
            <p className="text-sm text-slate-600 mt-2">{p.description}</p>
            {p.technologies && <p className="text-xs text-slate-500 mt-2">Tech: {p.technologies}</p>}
            <div className="flex items-center gap-4 text-xs text-slate-500 mt-3">
              <span>Started {formatDate(p.startDate)}</span>
              <span>Progress: {p.progressPct}%</span>
            </div>
            {(p.githubLink || p.liveLink) && (
              <div className="flex gap-3 mt-3 text-sm">
                {p.githubLink && <a href={p.githubLink} target="_blank" rel="noreferrer" className="text-brand-600 hover:underline">GitHub</a>}
                {p.liveLink && <a href={p.liveLink} target="_blank" rel="noreferrer" className="text-brand-600 hover:underline">Live Demo</a>}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
