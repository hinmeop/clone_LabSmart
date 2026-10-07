/** Khung response chung của backend — khớp common/response/ApiResponse.java. */
export interface ApiResponse<T> {
  success: boolean
  statusCode: number
  message: string
  data: T
  timestamp?: string
}

/**
 * Page<T> của Spring Data. Tuỳ cấu hình, Spring trả 1 trong 2 dạng:
 * - mặc định: { content, totalElements, totalPages, number, size, ... }
 * - VIA_DTO : { content, page: { size, number, totalElements, totalPages } }
 */
export interface SpringPage<T> {
  content: T[]
  totalElements?: number
  totalPages?: number
  number?: number
  size?: number
  page?: { size: number; number: number; totalElements: number; totalPages: number }
}

/** Dữ liệu phân trang đã chuẩn hoá dùng trong frontend (page bắt đầu từ 0). */
export interface PageResult<T> {
  items: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export function toPageResult<T>(raw: SpringPage<T>): PageResult<T> {
  const meta = raw.page ?? {
    number: raw.number ?? 0,
    size: raw.size ?? raw.content.length,
    totalElements: raw.totalElements ?? raw.content.length,
    totalPages: raw.totalPages ?? 1,
  }
  return {
    items: raw.content,
    page: meta.number,
    size: meta.size,
    totalElements: meta.totalElements,
    totalPages: meta.totalPages,
  }
}
