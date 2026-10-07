import { Avatar } from '../../../components/ui/Avatar'
import { Section } from '../../../components/ui/Section'
import { SectionHeading } from '../../../components/ui/SectionHeading'
import { ROUTES } from '../../../constants/routes'
import type { TeamMember } from '../../../types/home.types'
import { cn } from '../../../utils/format'

const ROLE_BADGE: Record<TeamMember['role'], { label: string; className: string }> = {
  manager: { label: 'Manager', className: 'bg-brand-100' },
  leader: { label: 'Leader', className: 'bg-brand-50' },
}

interface TeamSectionProps {
  members: TeamMember[]
}

export function TeamSection({ members }: TeamSectionProps) {
  return (
    <Section background="white">
      <SectionHeading
        eyebrow="Thành viên"
        title="Đội ngũ phụ trách"
        action={{ label: 'Tất cả thành viên', to: ROUTES.MEMBERS }}
      />

      <ul className="grid grid-cols-2 gap-3 sm:grid-cols-3 sm:gap-4 lg:grid-cols-6">
        {members.map((member) => {
          const badge = ROLE_BADGE[member.role]
          return (
            <li
              key={member.id}
              className="flex flex-col items-center gap-2.5 rounded-[14px] border border-line px-3 py-5 text-center sm:py-[22px]"
            >
              <Avatar name={member.fullName} src={member.avatarUrl} tone={member.tone} size="lg" />
              <span className="text-[15px] font-semibold sm:text-base">{member.fullName}</span>
              <span className="text-[13px] text-ink-500">{member.domainName}</span>
              <span className={cn('rounded-full px-2.5 py-0.5 text-xs font-semibold text-brand-600', badge.className)}>
                {badge.label}
              </span>
            </li>
          )
        })}
      </ul>
    </Section>
  )
}
