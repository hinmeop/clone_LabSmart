import type { ProjectStatus, Tone, UserRole } from './common.types'

export interface LabStat {
  label: string
  value: number
}

export interface DomainSummary {
  id: number
  code: string // chữ viết tắt hiển thị trong ô vuông (SE, AI, ...)
  name: string
  description: string
  managerName: string
}

export interface ProjectSummary {
  id: number
  name: string
  description: string
  domainName: string
  semester: string // VD: "Fall 2026"
  status: ProjectStatus
  startDate: string // ISO yyyy-mm-dd
  endDate: string
  leaderName: string
  memberCount: number
  thumbnailUrl?: string
  tone: Tone
}

export interface NewsSummary {
  id: number
  title: string
  excerpt: string
  publishedAt: string // ISO yyyy-mm-dd
  authorName: string
  coverImageUrl?: string
  tone: Tone
}

export interface TeamMember {
  id: number
  fullName: string
  domainName: string
  role: Extract<UserRole, 'manager' | 'leader'>
  avatarUrl?: string
  tone: Tone
}

export interface RecruitmentSummary {
  id: number
  title: string
  projectName: string
  quantity: number
  deadline: string // ISO yyyy-mm-dd
}

export interface ContactInfo {
  email: string
  phone: string
  address: string
}

export interface HomePageData {
  stats: LabStat[]
  domains: DomainSummary[]
  featuredProjects: ProjectSummary[]
  news: NewsSummary[]
  team: TeamMember[]
  recruitment: {
    semester: string
    posts: RecruitmentSummary[]
  }
}
