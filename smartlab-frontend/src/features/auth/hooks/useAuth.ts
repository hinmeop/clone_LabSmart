import { useContext } from 'react'
import { AuthContext } from '../context/AuthContext'
import type { AuthContextValue } from '../context/AuthContext'

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth phải được dùng bên trong <AuthProvider>')
  return ctx
}
