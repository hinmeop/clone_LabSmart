import { Link } from 'react-router-dom'
import { Avatar } from '../../../components/ui/Avatar'
import { StatusBadge } from '../../../components/ui/StatusBadge'
import { buildPath, ROUTES } from '../../../constants/routes'
import type { ProjectSummary } from '../../../types/home.types'
import { cn, formatDate } from '../../../utils/format'
import { TONE_BG } from '../../../utils/tone'

const LEADER_AVATAR_TONE = { mint: 'sand', sand: 'lavender', lavender: 'rose', rose: 'leaf', leaf: 'stone', stone: 'mint' } as const

interface ProjectCardProps {
  project: ProjectSummary
}

export function ProjectCard({ project }: ProjectCardProps) {
  return (
    <article className="group flex flex-col overflow-hidden rounded-[14px] border border-line bg-white transition hover:shadow-[0_8px_24px_-12px_rgba(21,32,29,0.25)]">
      {/* Thumbnail */}
      <div
        className={cn(
          'relative flex h-[140px] items-start justify-between gap-2 bg-cover bg-center p-4',
          !project.thumbnailUrl && TONE_BG[project.tone],
        )}
        style={project.thumbnailUrl ? { backgroundImage: `url(${project.thumbnailUrl})` } : undefined}
      >
        <span className="truncate rounded-md bg-white px-2.5 py-1 text-[13px] font-medium">
          {project.domainName} · {project.semester}
        </span>
        <StatusBadge status={project.status} />
      </div>

      {/* Nội dung */}
      <div className="flex grow flex-col gap-2.5 p-5">
        <h3 className="text-lg leading-[1.35] font-semibold sm:text-[19px]">
          <Link
            to={buildPath(ROUTES.PROJECT_DETAIL, { projectId: project.id })}
            className="text-ink-900 group-hover:text-brand-600"
          >
            {project.name}
          </Link>
        </h3>
        <p className="text-sm leading-relaxed text-ink-600">{project.description}</p>

        <div className="flex flex-wrap gap-x-4 gap-y-1 text-[13px] text-ink-500">
          <span>
            Bắt đầu <strong className="font-semibold text-ink-700">{formatDate(project.startDate)}</strong>
          </span>
          <span>
            Kết thúc <strong className="font-semibold text-ink-700">{formatDate(project.endDate)}</strong>
          </span>
        </div>

        <div className="mt-auto flex items-center justify-between gap-3 border-t border-line-soft pt-3.5">
          <span className="flex min-w-0 items-center gap-2 text-sm text-ink-700">
            <Avatar name={project.leaderName} tone={LEADER_AVATAR_TONE[project.tone]} />
            <span className="truncate">Leader · {project.leaderName}</span>
          </span>
          <span className="shrink-0 text-[13px] text-ink-500">{project.memberCount} thành viên</span>
        </div>
      </div>
    </article>
  )
}
