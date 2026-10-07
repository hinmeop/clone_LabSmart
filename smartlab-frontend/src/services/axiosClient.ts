import axios from 'axios'
import type { ApiResponse } from '../types/api.types'
import { authStorage } from '../utils/authStorage'

/**
 * Axios instance dùng chung cho mọi service gọi backend.
 * baseURL lấy từ biến VITE_API_BASE_URL trong file .env.
 */
if (!import.meta.env.VITE_API_BASE_URL) {
  console.error(
    '[axiosClient] Thiếu VITE_API_BASE_URL trong file .env → request sẽ gọi nhầm vào localhost:5173. ' +
      'Thêm dòng VITE_API_BASE_URL=http://localhost:8080/api/v1 rồi chạy lại npm run dev.',
  )
}

export const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
})

// Tự gắn JWT vào mọi request nếu đã đăng nhập
axiosClient.interceptors.request.use((config) => {
  const token = authStorage.getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

/** Lỗi gọi API đã chuẩn hoá: HTTP status (0 = không kết nối được) + message từ backend. */
export class ApiError extends Error {
  readonly status: number
  readonly fieldErrors?: Record<string, string>

  constructor(status: number, message: string, fieldErrors?: Record<string, string>) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

const LOGIN_URL = '/auth/login'

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError<ApiResponse<unknown>>(error)) {
      const status = error.response?.status ?? 0
      const body = error.response?.data

      // Token hết hạn / không hợp lệ → xoá phiên và quay về trang đăng nhập
      if (status === 401 && error.config?.url !== LOGIN_URL) {
        authStorage.clear()
        if (window.location.pathname !== '/login') window.location.assign('/login')
      }

      const fieldErrors =
        status === 400 && body?.data && typeof body.data === 'object' ? (body.data as Record<string, string>) : undefined
      return Promise.reject(new ApiError(status, body?.message ?? error.message, fieldErrors))
    }
    return Promise.reject(error)
  },
)

/** Lấy message tiếng Việt chung cho các lỗi phổ biến. */
export function getErrorMessage(error: unknown, fallback = 'Đã có lỗi xảy ra, vui lòng thử lại.'): string {
  if (!(error instanceof ApiError)) return fallback
  switch (error.status) {
    case 0:
      return 'Không kết nối được máy chủ. Vui lòng kiểm tra backend đã chạy chưa.'
    case 403:
      return 'Bạn không có quyền thực hiện thao tác này.'
    case 404:
      return 'Không tìm thấy dữ liệu (có thể đã bị xoá).'
    default:
      return error.status >= 500 ? 'Máy chủ gặp lỗi, vui lòng thử lại sau.' : fallback
  }
}
