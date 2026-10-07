/** Trạng thái danh mục — khớp enum CatalogStatus của backend. */
export type CatalogStatus = 'active' | 'inactive'

/** Khớp dto/response/DomainResponse.java */
export interface Domain {
  id: number
  name: string
  description: string | null
  status: CatalogStatus
  createdById: number | null
  createdByName: string | null
  updatedById: number | null
  updatedByName: string | null
  createdAt: string | null // "yyyy-MM-dd HH:mm:ss"
  updatedAt: string | null
}

/** Khớp CreateDomainRequest.java / UpdateDomainRequest.java */
export interface DomainPayload {
  name: string
  description?: string | null
}

export interface DomainListParams {
  page: number // bắt đầu từ 0
  size: number
  search?: string
}
