import { createContext, useContext, useState, ReactNode } from 'react'
import { api, getApiErrorMessage } from '../api/client'
import type { AuthUser } from '../types'

interface AuthContextValue {
  user: AuthUser | null
  loading: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })
  const [loading, setLoading] = useState(false)

  async function login(email: string, password: string) {
    setLoading(true)
    try {
      const res = await api.post('/auth/login', { email, password })
      const { accessToken, refreshToken, userId, email: userEmail, role, status, fullName } = res.data
      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
      const authUser: AuthUser = { userId, email: userEmail, role, status, fullName }
      localStorage.setItem('user', JSON.stringify(authUser))
      setUser(authUser)
    } catch (err) {
      throw new Error(getApiErrorMessage(err, 'Invalid email or password.'))
    } finally {
      setLoading(false)
    }
  }

  function logout() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
