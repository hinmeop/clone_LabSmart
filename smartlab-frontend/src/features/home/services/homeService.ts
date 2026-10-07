import type { HomePageData } from '../../../types/home.types'
import { HOME_MOCK_DATA } from '../data/home.mock'

/**
 * Lấy dữ liệu Trang chủ.
 * Hiện trả về mock; khi có API chỉ cần đổi thân hàm, VD:
 *   const res = await apiClient.get<HomePageData>('/public/home')
 *   return res.data
 */
export async function getHomePageData(): Promise<HomePageData> {
  return Promise.resolve(HOME_MOCK_DATA)
}
