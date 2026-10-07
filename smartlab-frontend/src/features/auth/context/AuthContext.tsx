import { createContext, useCallback, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { LOGIN_ALLOWED_ROLES } from '../../../constants/roles'
import type { AuthUser, LoginRequest } from '../../../types/auth.types'
import { AuthError, authService } from '../../../services/authService'
import { authStorage } from '../../../utils/authStorage'

export interface AuthContextValue {
  user: AuthUser | null
  isAuthenticated: boolean
  /** Tài khoản inactive (hiện backend chặn đăng nhập inactive nên luôn false). */
  isReadOnly: boolean
  login: (payload: LoginRequest) => Promise<AuthUser>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => authStorage.getUser())

  const login = useCallback(async (payload: LoginRequest) => {
    const { accessToken, expiresAt, user: loggedIn } = await authService.login(payload)

    // Quy tắc nghiệp vụ áp dụng chung cho mọi role
    if (loggedIn.status === 'locked') {
      throw new AuthError('Tài khoản đã bị khoá. Vui lòng liên hệ IT Admin.')
    }
    if (!LOGIN_ALLOWED_ROLES.includes(loggedIn.role)) {
      throw new AuthError('Tài khoản ứng viên không đăng nhập hệ thống nội bộ. Vui lòng theo dõi email từ Lab.')
    }

    authStorage.save(accessToken, expiresAt, loggedIn, payload.rememberMe)
    setUser(loggedIn)
    return loggedIn
  }, [])

  const logout = useCallback(() => {
    authStorage.clear()
    setUser(null)
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: user !== null,
      isReadOnly: user?.status === 'inactive',
      login,
      logout,
    }),
    [user, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
