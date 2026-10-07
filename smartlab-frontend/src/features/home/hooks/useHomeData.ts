import { useEffect, useState } from 'react'
import type { HomePageData } from '../../../types/home.types'
import { getHomePageData } from '../services/homeService'

interface UseHomeDataResult {
  data: HomePageData | null
  loading: boolean
  error: string | null
}

export function useHomeData(): UseHomeDataResult {
  const [state, setState] = useState<UseHomeDataResult>({ data: null, loading: true, error: null })

  useEffect(() => {
    let cancelled = false

    getHomePageData()
      .then((data) => {
        if (!cancelled) setState({ data, loading: false, error: null })
      })
      .catch(() => {
        if (!cancelled) setState({ data: null, loading: false, error: 'Không tải được dữ liệu trang chủ.' })
      })

    return () => {
      cancelled = true
    }
  }, [])

  return state
}
