import { ButtonLink } from '../components/ui/Button'
import { Container } from '../components/ui/Container'
import { ROUTES } from '../constants/routes'

export default function NotFoundPage() {
  return (
    <Container className="flex min-h-[60vh] flex-col items-center justify-center gap-4 py-16 text-center">
      <span className="text-5xl font-bold text-brand-600">404</span>
      <p className="text-lg text-ink-600">Không tìm thấy trang</p>
      <ButtonLink to={ROUTES.HOME}>Về trang chủ</ButtonLink>
    </Container>
  )
}
