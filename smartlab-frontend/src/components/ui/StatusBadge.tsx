import type { ProjectStatus } from '../../types/common.types'
import { cn } from '../../utils/format'

const STATUS_CONFIG: Record<ProjectStatus, { label: string; badge: string; dot: string }> = {
  pending: { label: 'Chưa bắt đầu', badge: 'bg-[#eef0ee] text-ink-700', dot: 'bg-ink-500' },
  processing: { label: 'Đang thực hiện', badge: 'bg-[#e3ecf6] text-[#1f4e84]', dot: 'bg-[#2f6fb5]' },
  done: { label: 'Hoàn thành', badge: 'bg-[#e3f3e8] text-[#1e6b3f]', dot: 'bg-[#2f8f5b]' },
  cancel: { label: 'Đã hủy', badge: 'bg-[#f8e4e2] text-[#8a2c22]', dot: 'bg-[#c2473a]' },
}

interface StatusBadgeProps {
  status: ProjectStatus
  className?: string
}

export function StatusBadge({ status, className }: StatusBadgeProps) {
  const config = STATUS_CONFIG[status]
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 whitespace-nowrap rounded-full px-2.5 py-0.5 text-[13px] font-semibold',
        config.badge,
        className,
      )}
    >
      <span className={cn('size-1.5 rounded-full', config.dot)} />
      {config.label}
    </span>
  )
}
