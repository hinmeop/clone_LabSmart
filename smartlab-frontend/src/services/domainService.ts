import type { ApiResponse, PageResult, SpringPage } from '../types/api.types'
import { toPageResult } from '../types/api.types'
import type { Domain, DomainListParams, DomainPayload } from '../types/domain.types'
import { axiosClient } from './axiosClient'

/**
 * Gọi API của DomainController (backend: @RequestMapping("/api/v1/domains")).
 * Prefix /api/v1 đã nằm trong VITE_API_BASE_URL.
 */
const DOMAIN_ENDPOINTS = {
  BASE: '/domains',
  BY_ID: (id: number) => `/domains/${id}`,
} as const

export const domainService = {
  /** GET /api/v1/domains?page=&size=&search= */
  async getList({ page, size, search = '' }: DomainListParams): Promise<PageResult<Domain>> {
    const res = await axiosClient.get<ApiResponse<SpringPage<Domain>>>(DOMAIN_ENDPOINTS.BASE, {
      params: { page, size, search: search.trim() },
    })
    return toPageResult(res.data.data)
  },

  /** POST /api/v1/domains */
  async create(payload: DomainPayload): Promise<Domain> {
    const res = await axiosClient.post<ApiResponse<Domain>>(DOMAIN_ENDPOINTS.BASE, payload)
    return res.data.data
  },

  /** PUT /api/v1/domains/{id} */
  async update(id: number, payload: DomainPayload): Promise<Domain> {
    const res = await axiosClient.put<ApiResponse<Domain>>(DOMAIN_ENDPOINTS.BY_ID(id), payload)
    return res.data.data
  },

  /** DELETE /api/v1/domains/{id} — xoá mềm */
  async remove(id: number): Promise<Domain> {
    const res = await axiosClient.delete<ApiResponse<Domain>>(DOMAIN_ENDPOINTS.BY_ID(id))
    return res.data.data
  },
}
