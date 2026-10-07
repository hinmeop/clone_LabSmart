import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { getPostLoginPath } from '../constants/roles'
import { useAuth } from '../features/auth/hooks/useAuth'

/** Trang chỉ dành cho người chưa đăng nhập (login, quên mật khẩu). Đã đăng nhập → chuyển tới khu vực theo role. */
export function GuestOnlyRoute() {
  const { user } = useAuth()
  const location = useLocation()
  if (user) {
    const from = (location.state as { from?: string } | null)?.from
    return <Navigate to={getPostLoginPath(user.role, from)} replace />
  }
  return <Outlet />
}
