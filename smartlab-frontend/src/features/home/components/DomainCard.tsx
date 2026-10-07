import { Link } from 'react-router-dom'
import { ChevronRightIcon } from '../../../components/icons'
import { buildPath, ROUTES } from '../../../constants/routes'
import type { DomainSummary } from '../../../types/home.types'

interface DomainCardProps {
  domain: DomainSummary
}

export function DomainCard({ domain }: DomainCardProps) {
  return (
    <Link
      to={buildPath(ROUTES.DOMAIN_DETAIL, { domainId: domain.id })}
      className="group flex flex-col gap-4 rounded-[14px] border border-line bg-white p-5 text-ink-900 transition hover:-translate-y-0.5 hover:border-brand-600/40 hover:shadow-[0_8px_24px_-12px_rgba(13,95,82,0.35)] sm:p-6"
    >
      <div className="flex items-center gap-3.5">
        <span className="flex size-[52px] shrink-0 items-center justify-center rounded-xl bg-brand-50 text-[15px] font-bold text-brand-600">
          {domain.code}
        </span>
        <div className="flex min-w-0 flex-col gap-0.5">
          <h3 className="text-lg font-semibold sm:text-[19px]">{domain.name}</h3>
          <span className="text-[13px] text-ink-500">Phụ trách: {domain.managerName}</span>
        </div>
      </div>

      <p className="text-[15px] leading-relaxed text-ink-600 sm:min-h-12">{domain.description}</p>

      <div className="mt-auto flex items-center border-t border-line-soft pt-4 text-sm">
        <span className="inline-flex items-center gap-1 font-semibold text-brand-600">
          Xem chi tiết
          <ChevronRightIcon size={15} className="transition-transform group-hover:translate-x-0.5" />
        </span>
      </div>
    </Link>
  )
}
