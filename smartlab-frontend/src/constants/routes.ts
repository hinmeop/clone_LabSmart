/**
 * Khai báo tập trung toàn bộ đường dẫn của ứng dụng.
 * Không hard-code chuỗi URL trong component — luôn import từ đây.
 */
export const ROUTES = {
  HOME: '/',

  // Xác thực (dùng chung cho mọi role)
  LOGIN: '/login',
  FORGOT_PASSWORD: '/forgot-password',
  FORBIDDEN: '/403',

  // Khu vực nội bộ sau đăng nhập — mỗi nhóm role có trang chủ riêng
  ADMIN: '/admin', // IT Admin
  BUSINESS: '/business', // Business Admin
  WORKSPACE: '/workspace', // Manager, Leader, Member
  WORKSPACE_DOMAINS: '/workspace/domains',

  DOMAINS: '/domains',
  DOMAIN_DETAIL: '/domains/:domainId',

  PROJECTS: '/projects',
  PROJECT_DETAIL: '/projects/:projectId',

  NEWS: '/news',
  NEWS_DETAIL: '/news/:newsId',

  MEMBERS: '/members',

  RECRUITMENT: '/recruitment',
  RECRUITMENT_DETAIL: '/recruitment/:postId',

  CONTACT: '/#contact',

  TEST: '/test',
} as const

/** Thay tham số `:param` trong route bằng giá trị thực. */
export function buildPath(path: string, params: Record<string, string | number>): string {
  return Object.entries(params).reduce(
    (acc, [key, value]) => acc.replace(`:${key}`, encodeURIComponent(String(value))),
    path,
  )
}

/** Menu điều hướng của khu vực công khai (Guest). */
export const PUBLIC_NAV_ITEMS = [
  { label: 'Trang chủ', to: ROUTES.HOME },
  { label: 'Lĩnh vực', to: ROUTES.DOMAINS },
  { label: 'Dự án', to: ROUTES.PROJECTS },
  { label: 'Tin tức', to: ROUTES.NEWS },
  { label: 'Thành viên', to: ROUTES.MEMBERS },
  { label: 'Tuyển dụng', to: ROUTES.RECRUITMENT },
] as const
