import { useState } from 'react'
import { Link, Outlet } from 'react-router-dom'
import { MenuIcon } from '../components/icons'
import { WorkspaceSidebar } from '../components/layout/WorkspaceSidebar'
import { ROUTES } from '../constants/routes'
import { SITE_LOGO, SITE_NAME } from '../constants/site'

const COLLAPSE_KEY = 'smartlab.sidebarCollapsed'

/** Layout khu vực nội bộ (Manager / Leader / Member): sidebar + nội dung. */
export default function WorkspaceLayout() {
  const [collapsed, setCollapsed] = useState(() => {
    try {
      return localStorage.getItem(COLLAPSE_KEY) === '1'
    } catch {
      return false
    }
  })
  const [mobileOpen, setMobileOpen] = useState(false)

  const toggleCollapse = () =>
    setCollapsed((v) => {
      try {
        localStorage.setItem(COLLAPSE_KEY, v ? '0' : '1')
      } catch {
        /* bỏ qua */
      }
      return !v
    })

  return (
    <div className="flex min-h-screen bg-canvas">
      <WorkspaceSidebar
        collapsed={collapsed}
        onToggleCollapse={toggleCollapse}
        mobileOpen={mobileOpen}
        onCloseMobile={() => setMobileOpen(false)}
      />

      <div className="flex min-w-0 flex-1 flex-col">
        {/* Thanh trên cho mobile/tablet */}
        <header className="sticky top-0 z-30 flex h-14 items-center gap-3 border-b border-line bg-white px-4 lg:hidden">
          <button
            type="button"
            aria-label="Mở menu"
            onClick={() => setMobileOpen(true)}
            className="flex size-10 items-center justify-center rounded-lg border border-line-strong"
          >
            <MenuIcon size={20} />
          </button>
          <Link to={ROUTES.WORKSPACE} className="flex items-center gap-2 text-ink-900">
            <img src={SITE_LOGO} alt="" className="size-7 object-contain" />
            <span className="font-semibold">{SITE_NAME}</span>
          </Link>
        </header>

        <main className="flex-1 px-4 py-5 sm:px-6 lg:px-8 lg:py-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
