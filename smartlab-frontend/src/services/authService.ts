import type { ApiResponse } from '../types/api.types'
import type { AuthUser, LoginApiRequest, LoginApiResponse, LoginRequest, LoginResult } from '../types/auth.types'
import { ApiError, axiosClient } from './axiosClient'

/**
 * Gọi API của AuthController (backend: @RequestMapping("/api/v1/auth")).
 * Prefix /api/v1 đã nằm trong VITE_API_BASE_URL.
 */
const AUTH_ENDPOINTS = {
  LOGIN: '/auth/login',
} as const

/** Lỗi đăng nhập — message tiếng Việt để hiển thị trên form. */
export class AuthError extends Error {}

function toLoginErrorMessage(error: ApiError): string {
  const message = error.message.toLowerCase()
  switch (error.status) {
    case 0:
      return 'Không kết nối được máy chủ. Vui lòng kiểm tra backend đã chạy chưa.'
    case 400:
      return 'Email hoặc mật khẩu không hợp lệ.'
    case 401:
      return 'Email hoặc mật khẩu không đúng.'
    case 403:
      if (message.includes('locked')) return 'Tài khoản đã bị khoá. Vui lòng liên hệ IT Admin.'
      if (message.includes('inactive')) return 'Tài khoản đang ngừng hoạt động. Vui lòng liên hệ IT Admin.'
      return 'Tài khoản không được phép đăng nhập.'
    default:
      return 'Không thể đăng nhập, vui lòng thử lại sau.'
  }
}

export const authService = {
  /** POST /api/v1/auth/login */
  async login({ email, password }: LoginRequest): Promise<LoginResult> {
    const body: LoginApiRequest = { orgEmail: email.trim().toLowerCase(), password }
    try {
      const res = await axiosClient.post<ApiResponse<LoginApiResponse>>(AUTH_ENDPOINTS.LOGIN, body)
      const { accessToken, expiresIn, user } = res.data.data

      const authUser: AuthUser = {
        id: user.id,
        fullName: user.fullName,
        orgEmail: user.orgEmail,
        role: user.role,
        status: user.status,
        avatarUrl: user.avatarUrl ?? undefined,
        department: user.department ?? undefined,
        phone: user.phone ?? undefined,
      }
      return { accessToken, expiresAt: Date.now() + expiresIn, user: authUser }
    } catch (error) {
      if (error instanceof ApiError) throw new AuthError(toLoginErrorMessage(error))
      throw error
    }
  },
}
