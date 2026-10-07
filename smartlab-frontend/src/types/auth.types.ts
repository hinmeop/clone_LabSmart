import type { UserRole } from './common.types'

/** Trạng thái tài khoản — khớp enum UserStatus của backend. */
export type UserStatus = 'active' | 'inactive' | 'locked'

/** User đang đăng nhập, lưu ở frontend. */
export interface AuthUser {
  id: number
  fullName: string
  orgEmail: string
  role: UserRole
  status: UserStatus
  avatarUrl?: string
  department?: string
  phone?: string
}

/** Dữ liệu form đăng nhập. */
export interface LoginRequest {
  email: string
  password: string
  rememberMe: boolean
}

/** Kết quả đăng nhập dùng trong frontend. */
export interface LoginResult {
  accessToken: string
  /** Thời điểm token hết hạn (epoch ms). */
  expiresAt: number
  user: AuthUser
}

/* ---------- DTO backend (POST /api/v1/auth/login) ---------- */

/** Khớp dto/request/LoginRequest.java */
export interface LoginApiRequest {
  orgEmail: string
  password: string
}

/** Khớp dto/response/UserProfileResponse.java */
export interface UserProfileDto {
  id: number
  orgEmail: string
  fullName: string
  dateOfBirth?: string | null
  department?: string | null
  note?: string | null
  avatarUrl?: string | null
  phone?: string | null
  role: UserRole
  status: UserStatus
  createdAt?: string
  updatedAt?: string
}

/** Khớp dto/response/LoginResponse.java */
export interface LoginApiResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: UserProfileDto
}
