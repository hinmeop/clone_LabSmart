import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import { ArrowLeftIcon, ShieldIcon } from '../../../components/icons'
import { ROUTES } from '../../../constants/routes'
import { SITE_LOGO, SITE_NAME } from '../../../constants/site'

const HIGHLIGHTS = [
  'Một cổng đăng nhập cho mọi vai trò trong Lab',
  'Tự động chuyển tới khu vực làm việc theo quyền của bạn',
  'Tài khoản do IT Admin cấp — dùng email tổ chức',
]

/** Khung trang xác thực: panel thương hiệu bên trái (desktop) + form bên phải. */
export function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="grid min-h-screen grid-cols-1 bg-canvas lg:grid-cols-2">
      {/* Panel thương hiệu — ẩn trên mobile/tablet */}
      <aside className="relative hidden overflow-hidden bg-ink-900 text-canvas lg:flex lg:flex-col lg:justify-between lg:p-14">
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -top-40 -right-40 size-[520px] rounded-full bg-brand-600/40 blur-3xl"
        />
        <Link to={ROUTES.HOME} className="relative flex items-center gap-3 text-white">
          <img src={SITE_LOGO} alt="" className="size-11 rounded-xl bg-white/95 object-contain p-1" />
          <span className="text-2xl font-bold">{SITE_NAME}</span>
        </Link>

        <div className="relative flex max-w-[480px] flex-col gap-6">
          <h1 className="text-[40px] leading-[1.15] font-bold tracking-[-0.02em]">
            Quản lý vận hành phòng Lab, tất cả ở một nơi.
          </h1>
          <ul className="flex flex-col gap-3">
            {HIGHLIGHTS.map((text) => (
              <li key={text} className="flex items-start gap-3 text-[15px] text-ink-200">
                <ShieldIcon size={18} className="mt-0.5 shrink-0 text-brand-300" />
                {text}
              </li>
            ))}
          </ul>
        </div>

        <span className="relative text-sm text-ink-300">© {new Date().getFullYear()} {SITE_NAME}</span>
      </aside>

      {/* Khu vực form */}
      <main className="flex flex-col px-4 py-6 sm:px-8 lg:px-14">
        <div className="flex items-center justify-between">
          <Link to={ROUTES.HOME} className="flex items-center gap-2 text-ink-900 lg:hidden">
            <img src={SITE_LOGO} alt="" className="size-9 object-contain" />
            <span className="text-xl font-bold">{SITE_NAME}</span>
          </Link>
          <Link
            to={ROUTES.HOME}
            className="ml-auto inline-flex items-center gap-1.5 text-sm font-medium text-ink-600 hover:text-brand-600"
          >
            <ArrowLeftIcon size={16} />
            Về trang chủ
          </Link>
        </div>

        <div className="flex flex-1 items-center justify-center py-10">
          <div className="w-full max-w-[420px]">{children}</div>
        </div>
      </main>
    </div>
  )
}
