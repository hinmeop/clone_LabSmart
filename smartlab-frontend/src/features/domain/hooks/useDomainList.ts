import { useCallback, useEffect, useState } from 'react'
import { getErrorMessage } from '../../../services/axiosClient'
import { domainService } from '../../../services/domainService'
import type { PageResult } from '../../../types/api.types'
import type { Domain } from '../../../types/domain.types'

const EMPTY: PageResult<Domain> = { items: [], page: 0, size: 10, totalElements: 0, totalPages: 0 }

/** Quản lý trạng thái danh sách domain: phân trang, tìm kiếm (debounce 400ms), tải lại. */
export function useDomainList() {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(10)
  const [searchInput, setSearchInput] = useState('')
  const [search, setSearch] = useState('')
  const [data, setData] = useState<PageResult<Domain>>(EMPTY)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [reloadKey, setReloadKey] = useState(0)

  // Debounce ô tìm kiếm
  useEffect(() => {
    const t = setTimeout(() => {
      setSearch(searchInput.trim())
      setPage(0)
    }, 400)
    return () => clearTimeout(t)
  }, [searchInput])

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)
    domainService
      .getList({ page, size, search })
      .then((result) => {
        if (cancelled) return
        // Trang hiện tại trống sau khi xoá → lùi về trang trước
        if (result.items.length === 0 && page > 0) setPage(page - 1)
        else setData(result)
      })
      .catch((err) => {
        if (!cancelled) setError(getErrorMessage(err, 'Không tải được danh sách domain.'))
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [page, size, search, reloadKey])

  const reload = useCallback(() => setReloadKey((k) => k + 1), [])

  const reset = useCallback(() => {
    setSearchInput('')
    setSearch('')
    setPage(0)
  }, [])

  return {
    data,
    loading,
    error,
    page,
    size,
    searchInput,
    setSearchInput,
    setPage,
    setSize: (s: number) => {
      setSize(s)
      setPage(0)
    },
    reload,
    reset,
  }
}
