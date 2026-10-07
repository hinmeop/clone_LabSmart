import { NavLink, useNavigate } from 'react-router-dom'
import { ROLE_LABELS } from '../../constants/roles'
import { ROUTES } from '../../constants/routes'
import { SITE_LOGO, SITE_NAME } from '../../constants/site'
import { WORKSPACE_NAV } from '../../constants/workspaceNav'
import { useAuth } from '../../features/auth/hooks/useAuth'
import { cn } from '../../utils/format'
import { CloseIcon, LogOutIcon, PanelLeftIcon } from '../icons'
import { Avatar } from '../ui/Avatar'

interface WorkspaceSidebarProps {
  /** Thu gọn (chỉ hiện icon) — chỉ áp dụng trên desktop */
  collapsed: boolean
  onToggleCollapse: () => void
  /** Đang mở dạng drawer trên mobile/tablet */
  mobileOpen: boolean
  onCloseMobile: () => void
}

export function WorkspaceSidebar({ collapsed, onToggleCollapse, mobileOpen, onCloseMobile }: WorkspaceSidebarProps) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  if (!user) return null

  // Trên mobile luôn hiện đầy đủ chữ
  const hideText = collapsed && !mobileOpen

  return (
    <>
      {/* Lớp nền khi mở drawer trên mobile */}
      {mobileOpen && (
        <button
          type="button"
          aria-label="Đóng menu"
          onClick={onCloseMobile}
          className="fixed inset-0 z-40 bg-ink-900/40 lg:hidden"
        />
      )}

      <aside
        className={cn(
          'fixed inset-y-0 left-0 z-50 flex w-[264px] flex-col border-r border-line bg-white text-ink-700 transition-[transform,width] duration-200',
          mobileOpen ? 'translate-x-0' : '-translate-x-full',
          'lg:sticky lg:top-0 lg:h-screen lg:translate-x-0',
          collapsed && 'lg:w-[72px]',
        )}
      >
        {/* Logo + nút thu gọn */}
        <div className={cn('flex h-16 shrink-0 items-center gap-2.5 border-b border-line', hideText ? 'justify-center px-2' : 'pr-3 pl-5')}>
          {!hideText && (
            <NavLink to={ROUTES.HOME} className="flex min-w-0 items-center gap-2.5 text-ink-900">
              <img src={SITE_LOGO} alt="" className="size-8 shrink-0 object-contain" />
              <span className="text-xl font-semibold">{SITE_NAME}</span>
            </NavLink>
          )}
          <button
            type="button"
            onClick={onToggleCollapse}
            aria-label={collapsed ? 'Mở rộng sidebar' : 'Thu gọn sidebar'}
            className="ml-auto hidden size-9 items-center justify-center rounded-lg border border-line text-ink-600 hover:text-brand-600 lg:flex"
            style={hideText ? { marginLeft: 0 } : undefined}
          >
            <PanelLeftIcon size={18} />
          </button>
          <button
            type="button"
            onClick={onCloseMobile}
            aria-label="Đóng menu"
            className="ml-auto flex size-9 items-center justify-center rounded-lg border border-line text-ink-600 lg:hidden"
          >
            <CloseIcon size={18} />
          </button>
        </div>

        {/* Menu */}
        <nav aria-label="Điều hướng workspace" className="flex grow flex-col gap-6 overflow-y-auto px-3 py-5 text-sm">
          {WORKSPACE_NAV.map((group, gi) => {
            const items = group.items.filter((item) => item.roles.includes(user.role))
            if (items.length === 0) return null
            return (
              <div key={group.title ?? gi} className="flex flex-col gap-0.5">
                {group.title && !hideText && (
                  <span className="px-3 pb-2 text-[11px] tracking-[0.08em] text-ink-500 uppercase">{group.title}</span>
                )}
                {items.map(({ label, to, icon: Icon, end }) => (
                  <NavLink
                    key={to}
                    to={to}
                    end={end}
                    title={hideText ? label : undefined}
                    onClick={onCloseMobile}
                    className={({ isActive }) =>
                      cn(
                        'flex h-10 items-center gap-3 rounded-lg px-3 transition-colors',
                        hideText && 'justify-center px-0',
                        isActive ? 'bg-brand-50 font-semibold text-brand-600' : 'text-ink-700 hover:bg-canvas hover:text-ink-900',
                      )
                    }
                  >
                    <Icon size={18} className="shrink-0" />
                    {!hideText && <span className="truncate">{label}</span>}
                  </NavLink>
                ))}
              </div>
            )
          })}
        </nav>

        {/* Người dùng */}
        <div className={cn('flex items-center gap-3 border-t border-line py-4', hideText ? 'flex-col px-2' : 'px-5')}>
          <Avatar name={user.fullName} src={user.avatarUrl} size="md" />
          {!hideText && (
            <div className="flex min-w-0 flex-col">
              <span className="truncate text-sm font-semibold text-ink-900">{user.fullName}</span>
              <span className="text-xs text-ink-500">{ROLE_LABELS[user.role]}</span>
            </div>
          )}
          <button
            type="button"
            aria-label="Đăng xuất"
            title="Đăng xuất"
            onClick={() => {
              logout()
              navigate(ROUTES.LOGIN, { replace: true })
            }}
            className={cn('flex size-9 items-center justify-center rounded-lg text-ink-500 hover:bg-canvas hover:text-[#b3261e]', !hideText && 'ml-auto')}
          >
            <LogOutIcon size={18} />
          </button>
        </div>
      </aside>
    </>
  )
}
