/** Tông màu pastel dùng cho avatar, thumbnail khi chưa có ảnh. */
export type Tone = 'mint' | 'sand' | 'lavender' | 'rose' | 'leaf' | 'stone'

/** Vai trò toàn cục — khớp cột users.role. */
export type UserRole = 'it_admin' | 'business_admin' | 'manager' | 'leader' | 'member' | 'guest'

/** Trạng thái dự án — khớp cột projects.status. */
export type ProjectStatus = 'pending' | 'processing' | 'done' | 'cancel'
