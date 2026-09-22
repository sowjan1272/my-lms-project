import { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import type { Role } from '../types'

interface Props {
  allowedRoles: Role[]
  children: ReactNode
}

export default function ProtectedRoute({ allowedRoles, children }: Props) {
  const { user } = useAuth()

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (!allowedRoles.includes(user.role)) {
    return <Navigate to={`/${user.role.toLowerCase()}/dashboard`} replace />
  }

  return <>{children}</>
}
