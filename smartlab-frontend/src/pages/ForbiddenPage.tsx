import { ButtonLink } from '../components/ui/Button'
import { Container } from '../components/ui/Container'
import { ROLE_HOME } from '../constants/roles'
import { ROUTES } from '../constants/routes'
import { useAuth } from '../features/auth'

export default function ForbiddenPage() {
  const { user } = useAuth()
  return (
    <Container className="flex min-h-screen flex-col items-center justify-center gap-4 text-center">
      <span className="text-5xl font-bold text-brand-600">403</span>
      <p className="text-lg text-ink-600">Bạn không có quyền truy cập trang này.</p>
      <ButtonLink to={user ? ROLE_HOME[user.role] : ROUTES.HOME}>Về trang của tôi</ButtonLink>
    </Container>
  )
}
