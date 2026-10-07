import type { AuthUser } from '../types/auth.types'

const TOKEN_KEY = 'smartlab.accessToken'
const EXPIRES_KEY = 'smartlab.expiresAt'
const USER_KEY = 'smartlab.user'

function safe<T>(fn: () => T, fallback: T): T {
  try {
    return fn()
  } catch {
    return fallback
  }
}

function read(key: string): string | null {
  return safe(() => localStorage.getItem(key) ?? sessionStorage.getItem(key), null)
}

/**
 * Lưu phiên đăng nhập ở trình duyệt.
 * - "Ghi nhớ đăng nhập" → localStorage (giữ sau khi đóng trình duyệt)
 * - Không ghi nhớ → sessionStorage (mất khi đóng tab)
 * Token quá hạn (theo expiresIn của backend) được coi như chưa đăng nhập.
 */
export const authStorage = {
  save(token: string, expiresAt: number, user: AuthUser, remember: boolean) {
    authStorage.clear()
    safe(() => {
      const store = remember ? localStorage : sessionStorage
      store.setItem(TOKEN_KEY, token)
      store.setItem(EXPIRES_KEY, String(expiresAt))
      store.setItem(USER_KEY, JSON.stringify(user))
    }, undefined)
  },

  getToken(): string | null {
    const token = read(TOKEN_KEY)
    const expiresAt = Number(read(EXPIRES_KEY))
    if (!token || !expiresAt || Date.now() >= expiresAt) return null
    return token
  },

  getUser(): AuthUser | null {
    if (!authStorage.getToken()) return null
    return safe(() => {
      const raw = read(USER_KEY)
      return raw ? (JSON.parse(raw) as AuthUser) : null
    }, null)
  },

  clear() {
    safe(() => {
      for (const store of [localStorage, sessionStorage]) {
        store.removeItem(TOKEN_KEY)
        store.removeItem(EXPIRES_KEY)
        store.removeItem(USER_KEY)
      }
    }, undefined)
  },
}
