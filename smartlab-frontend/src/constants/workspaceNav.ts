import type { ComponentType, SVGProps } from 'react'
import {
  BellIcon,
  BriefcaseIcon,
  DashboardIcon,
  EditNoteIcon,
  FileIcon,
  KanbanIcon,
  LayersIcon,
  NewspaperIcon,
  UserIcon,
  UsersIcon,
} from '../components/icons'
import type { UserRole } from '../types/common.types'
import { ROUTES } from './routes'

export interface NavItem {
  label: string
  to: string
  icon: ComponentType<SVGProps<SVGSVGElement> & { size?: number }>
  /** Role được thấy mục này */
  roles: UserRole[]
  end?: boolean
}

export interface NavGroup {
  title?: string
  items: NavItem[]
}

const ALL: UserRole[] = ['manager', 'leader', 'member']

/** Menu sidebar khu vực Workspace — theo mockup Sidebar Manager. */
export const WORKSPACE_NAV: NavGroup[] = [
  { items: [{ label: 'Dashboard', to: ROUTES.WORKSPACE, icon: DashboardIcon, roles: ALL, end: true }] },
  {
    title: 'Dự án',
    items: [
      { label: 'Domain & Dự án', to: ROUTES.WORKSPACE_DOMAINS, icon: LayersIcon, roles: ['manager'] },
      { label: 'Dự án & Task', to: `${ROUTES.WORKSPACE}/tasks`, icon: KanbanIcon, roles: ALL },
    ],
  },
  {
    title: 'Nhân sự',
    items: [
      { label: 'Quản lý người dùng', to: `${ROUTES.WORKSPACE}/users`, icon: UsersIcon, roles: ['manager'] },
      { label: 'Tuyển dụng (HR)', to: `${ROUTES.WORKSPACE}/recruitment`, icon: BriefcaseIcon, roles: ['manager'] },
    ],
  },
  {
    title: 'Nội dung',
    items: [
      { label: 'Tài liệu', to: `${ROUTES.WORKSPACE}/materials`, icon: FileIcon, roles: ALL },
      { label: 'Tin tức', to: `${ROUTES.WORKSPACE}/news`, icon: NewspaperIcon, roles: ALL },
      { label: 'Bài viết', to: `${ROUTES.WORKSPACE}/posts`, icon: EditNoteIcon, roles: ALL },
    ],
  },
  {
    title: 'Cá nhân',
    items: [
      { label: 'Thông báo', to: `${ROUTES.WORKSPACE}/notifications`, icon: BellIcon, roles: ALL },
      { label: 'Hồ sơ', to: `${ROUTES.WORKSPACE}/profile`, icon: UserIcon, roles: ALL },
    ],
  },
]
