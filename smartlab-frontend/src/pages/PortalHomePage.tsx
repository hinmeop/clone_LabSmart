import { useNavigate } from 'react-router-dom'
import { LogOutIcon } from '../components/icons'
import { Avatar } from '../components/ui/Avatar'
import { Button, ButtonLink } from '../components/ui/Button'
import { Container } from '../components/ui/Container'
import { ROLE_LABELS } from '../constants/roles'
import { ROUTES } from '../constants/routes'
import { useAuth } from '../features/auth'

/**
 * Trang tạm cho khu vực nội bộ (/admin, /business, /workspace).
 * Sẽ được thay bằng layout có sidebar theo mockup Sidebar_Manager / IT Admin.
 */
export default function PortalHomePage({ area }: { area: string }) {
  const { user, isReadOnly, logout } = useAuth()
  const navigate = useNavigate()
  if (!user) return null

  return (
    <Container className="flex min-h-screen flex-col items-center justify-center gap-6 py-16 text-center">
      <Avatar name={user.fullName} size="lg" />
      <div className="flex flex-col gap-1">
        <span className="text-sm font-semibold text-brand-600">{area}</span>
        <h1 className="text-3xl font-bold">Xin chào, {user.fullName}</h1>
        <p className="text-ink-600">
          {user.orgEmail} · Vai trò <strong>{ROLE_LABELS[user.role]}</strong>
        </p>
      </div>
      {isReadOnly && (
        <p className="rounded-lg bg-[#fff4de] px-4 py-2 text-sm text-[#7a5200]">
          Tài khoản đang ở trạng thái ngừng hoạt động — bạn chỉ có quyền xem.
        </p>
      )}
      <div className="flex flex-wrap justify-center gap-3">
        <ButtonLink to={ROUTES.HOME} variant="outline">
          Xem trang chủ
        </ButtonLink>
        <Button
          leftIcon={<LogOutIcon />}
          onClick={() => {
            logout()
            navigate(ROUTES.LOGIN, { replace: true })
          }}
        >
          Đăng xuất
        </Button>
      </div>
    </Container>
  )
}
