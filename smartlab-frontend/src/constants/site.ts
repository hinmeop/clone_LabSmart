import type { ContactInfo } from '../types/home.types'

export const SITE_NAME = 'SmartLab'

/** Logo đặt trong thư mục public/ → được phục vụ tại đường dẫn gốc. */
export const SITE_LOGO = '/logo.png'

/**
 * Thông tin liên hệ hiển thị ở footer.
 * TODO: thay bằng dữ liệu system_settings (lab_contact_email, lab_contact_address) khi có API.
 */
export const LAB_CONTACT: ContactInfo = {
  email: 'smartlab@fpt.edu.vn',
  phone: '024 7300 1866',
  address: 'Phòng Lab 402, Tòa Alpha, ĐH FPT Hà Nội',
}

/** Vị trí Trường Đại học FPT – cơ sở Hà Nội (Khu CNC Hòa Lạc) trên Google Maps. */
const MAP_QUERY = encodeURIComponent('Trường Đại học FPT Hà Nội, Khu Công nghệ cao Hòa Lạc, Thạch Thất, Hà Nội')

export const LAB_MAP = {
  /** Nhúng bản đồ (không cần API key). `iwloc=` tắt thẻ thông tin để không che bản đồ. */
  embedUrl: `https://maps.google.com/maps?q=${MAP_QUERY}&hl=vi&t=m&z=16&ie=UTF8&iwloc=&output=embed`,
  /** Mở Google Maps ở tab mới khi người dùng bấm vào bản đồ. */
  linkUrl: `https://www.google.com/maps/search/?api=1&query=${MAP_QUERY}`,
}
