import type { UserRole } from '../types/common.types'
import { ROUTES } from './routes'

/**
 * Giá trị role khớp enum backend:
 * IT_ADMIN("it_admin"), BUSINESS_ADMIN("business_admin"), MANAGER("manager"),
 * LEADER("leader"), MEMBER("member"), GUEST("guest")
 */
export const ROLES = {
  IT_ADMIN: 'it_admin',
  BUSINESS_ADMIN: 'business_admin',
  MANAGER: 'manager',
  LEADER: 'leader',
  MEMBER: 'member',
  GUEST: 'guest',
} as const satisfies Record<string, UserRole>

export const ROLE_LABELS: Record<UserRole, string> = {
  it_admin: 'IT Admin',
  business_admin: 'Business Admin',
  manager: 'Manager',
  leader: 'Leader',
  member: 'Member',
  guest: 'Guest',
}

/** Các role được phép đăng nhập vào hệ thống nội bộ (Guest là ứng viên, không có tài khoản nội bộ). */
export const LOGIN_ALLOWED_ROLES: UserRole[] = ['it_admin', 'business_admin', 'manager', 'leader', 'member']

/** Nhóm role theo khu vực nội bộ. */
export const AREA_ROLES = {
  ADMIN: ['it_admin'],
  BUSINESS: ['business_admin'],
  WORKSPACE: ['manager', 'leader', 'member'],
} as const satisfies Record<string, readonly UserRole[]>

/** Trang đích sau khi đăng nhập theo role. */
export const ROLE_HOME: Record<UserRole, string> = {
  it_admin: ROUTES.ADMIN,
  business_admin: ROUTES.BUSINESS,
  manager: ROUTES.WORKSPACE,
  leader: ROUTES.WORKSPACE,
  member: ROUTES.WORKSPACE,
  guest: ROUTES.HOME,
}

/**
 * Trang đích sau đăng nhập: quay lại trang người dùng định vào (nếu thuộc khu vực của role),
 * ngược lại về trang chủ theo role.
 */
export function getPostLoginPath(role: UserRole, from?: string | null): string {
  const home = ROLE_HOME[role]
  return from && from.startsWith(home) ? from : home
}
