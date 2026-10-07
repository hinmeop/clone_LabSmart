import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { ROUTES } from '../constants/routes'
import { useAuth } from '../features/auth/hooks/useAuth'
import type { UserRole } from '../types/common.types'

interface ProtectedRouteProps {
  /** Role được phép vào nhánh route này. Bỏ trống = mọi user đã đăng nhập. */
  allowedRoles?: readonly UserRole[]
}

/**
 * Chặn route nội bộ:
 * - Chưa đăng nhập → chuyển về /login, nhớ trang đang muốn vào để quay lại sau khi đăng nhập.
 * - Sai role → /403.
 */
export function ProtectedRoute({ allowedRoles }: ProtectedRouteProps) {
  const { user } = useAuth()
  const location = useLocation()

  if (!user) {
    return <Navigate to={ROUTES.LOGIN} replace state={{ from: location.pathname }} />
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to={ROUTES.FORBIDDEN} replace />
  }

  return <Outlet />
}
