import { Link } from 'react-router-dom'
import { ROUTES } from '../../constants/routes'
import { SITE_LOGO, SITE_NAME } from '../../constants/site'

export function Logo() {
  return (
    <Link to={ROUTES.HOME} className="flex items-center gap-2.5 text-ink-900" aria-label={`${SITE_NAME} – Trang chủ`}>
      <img src={SITE_LOGO} alt="" width={40} height={40} className="size-10 shrink-0 object-contain" />
      <span className="text-xl font-bold sm:text-[22px]">{SITE_NAME}</span>
    </Link>
  )
}
