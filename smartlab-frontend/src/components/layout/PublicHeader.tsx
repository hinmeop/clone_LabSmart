import { useState } from 'react'
import { NavLink } from 'react-router-dom'
import { ROLE_HOME } from '../../constants/roles'
import { PUBLIC_NAV_ITEMS, ROUTES } from '../../constants/routes'
import { useAuth } from '../../features/auth/hooks/useAuth'
import { cn } from '../../utils/format'
import { CloseIcon, MenuIcon, UserIcon } from '../icons'
import { ButtonLink } from '../ui/Button'
import { Container } from '../ui/Container'
import { Logo } from './Logo'

function navLinkClass({ isActive }: { isActive: boolean }) {
  return cn(
    'rounded-lg px-3.5 py-2.5 text-[15px] font-medium transition-colors',
    isActive ? 'bg-brand-50 text-brand-600' : 'text-ink-700 hover:bg-canvas hover:text-brand-600',
  )
}

export function PublicHeader() {
  const [menuOpen, setMenuOpen] = useState(false)
  const closeMenu = () => setMenuOpen(false)
  const { user } = useAuth()

  // Đã đăng nhập → nút "Vào hệ thống" dẫn tới khu vực theo role; chưa đăng nhập → "Đăng nhập"
  const account = user
    ? { to: ROLE_HOME[user.role], label: 'Vào hệ thống' }
    : { to: ROUTES.LOGIN, label: 'Đăng nhập' }

  return (
    <header className="sticky top-0 z-40 border-b border-line bg-white/95 backdrop-blur">
      <Container className="flex h-16 items-center justify-between gap-4 lg:h-[72px]">
        <Logo />

        {/* Menu desktop */}
        <nav aria-label="Điều hướng chính" className="hidden items-center gap-1 lg:flex">
          {PUBLIC_NAV_ITEMS.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.to === ROUTES.HOME} className={navLinkClass}>
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="flex items-center gap-2">
          <div className="hidden sm:block">
            <ButtonLink to={account.to} leftIcon={<UserIcon />}>
              {account.label}
            </ButtonLink>
          </div>

          {/* Nút mở menu mobile/tablet */}
          <button
            type="button"
            onClick={() => setMenuOpen((open) => !open)}
            aria-expanded={menuOpen}
            aria-controls="mobile-nav"
            aria-label={menuOpen ? 'Đóng menu' : 'Mở menu'}
            className="inline-flex size-11 items-center justify-center rounded-lg border border-line-strong text-ink-900 lg:hidden"
          >
            {menuOpen ? <CloseIcon size={20} /> : <MenuIcon size={20} />}
          </button>
        </div>
      </Container>

      {/* Menu mobile/tablet */}
      <div id="mobile-nav" className={cn('border-t border-line bg-white lg:hidden', menuOpen ? 'block' : 'hidden')}>
        <Container className="flex flex-col gap-1 py-3">
          {PUBLIC_NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === ROUTES.HOME}
              className={navLinkClass}
              onClick={closeMenu}
            >
              {item.label}
            </NavLink>
          ))}
          <ButtonLink to={account.to} leftIcon={<UserIcon />} className="mt-2 sm:hidden" onClick={closeMenu}>
            {account.label}
          </ButtonLink>
        </Container>
      </div>
    </header>
  )
}
