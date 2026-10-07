import type { CatalogStatus } from '../../types/domain.types'
import { cn } from '../../utils/format'

const CONFIG: Record<CatalogStatus, { label: string; badge: string; dot: string }> = {
  active: { label: 'Active', badge: 'bg-[#e3f3e8] text-[#1e6b3f]', dot: 'bg-[#2f8f5b]' },
  inactive: { label: 'Deactive', badge: 'bg-[#eceeea] text-ink-600', dot: 'bg-[#8a9692]' },
}

export function CatalogStatusBadge({ status }: { status: CatalogStatus }) {
  const c = CONFIG[status] ?? CONFIG.inactive
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 whitespace-nowrap rounded-full px-2.5 py-0.5 text-[13px] font-semibold',
        c.badge,
      )}
    >
      <span className={cn('size-1.5 rounded-full', c.dot)} />
      {c.label}
    </span>
  )
}
