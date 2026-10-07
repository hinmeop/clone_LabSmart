import { PencilIcon } from '../../../components/icons'
import { CatalogStatusBadge } from '../../../components/ui/CatalogStatusBadge'
import type { Domain } from '../../../types/domain.types'
import { formatDate } from '../../../utils/format'
import { getDomainCode } from '../utils/domainCode'

interface DomainTableProps {
  domains: Domain[]
  loading: boolean
  onEdit: (domain: Domain) => void
}

function DomainBadge({ name }: { name: string }) {
  return (
    <span className="flex size-10 shrink-0 items-center justify-center rounded-[10px] bg-brand-50 text-xs font-semibold text-brand-600">
      {getDomainCode(name)}
    </span>
  )
}

function EditButton({ domain, onEdit }: { domain: Domain; onEdit: (d: Domain) => void }) {
  return (
    <button
      type="button"
      aria-label={`Sửa ${domain.name}`}
      title="Sửa domain"
      onClick={() => onEdit(domain)}
      className="flex size-9 items-center justify-center rounded-lg text-ink-600 hover:bg-brand-50 hover:text-brand-600"
    >
      <PencilIcon size={17} />
    </button>
  )
}

/** Bảng domain trên desktop, chuyển sang dạng thẻ trên mobile. */
export function DomainTable({ domains, loading, onEdit }: DomainTableProps) {
  if (loading && domains.length === 0) {
    return (
      <div className="flex flex-col">
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="flex h-16 items-center gap-3 border-t border-line-soft px-4 first:border-t-0">
            <span className="size-10 animate-pulse rounded-[10px] bg-canvas" />
            <span className="h-4 w-1/3 animate-pulse rounded bg-canvas" />
          </div>
        ))}
      </div>
    )
  }

  if (domains.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center gap-1 px-4 py-16 text-center">
        <p className="font-semibold text-ink-900">Chưa có domain nào</p>
        <p className="text-sm text-ink-500">Thử đổi từ khoá tìm kiếm hoặc bấm “Thêm domain”.</p>
      </div>
    )
  }

  return (
    <div className={loading ? 'opacity-60 transition-opacity' : undefined}>
      {/* Desktop / tablet ngang */}
      <table className="hidden w-full border-collapse text-sm md:table">
        <thead>
          <tr className="h-11 bg-[#f8f9f7] text-left text-xs font-semibold tracking-[0.02em] text-ink-500">
            <th className="px-3 pl-4">DOMAIN</th>
            <th className="w-[190px] px-3 whitespace-nowrap">NGƯỜI TẠO</th>
            <th className="w-[130px] px-3">TRẠNG THÁI</th>
            <th className="w-[120px] px-3">NGÀY TẠO</th>
            <th className="w-[96px] px-4 text-center whitespace-nowrap">THAO TÁC</th>
          </tr>
        </thead>
        <tbody>
          {domains.map((domain) => (
            <tr key={domain.id} className="h-16 border-t border-line-soft hover:bg-[#f3f7f5]">
              <td className="py-2 pr-3 pl-4">
                <div className="flex items-center gap-3">
                  <DomainBadge name={domain.name} />
                  <div className="flex min-w-0 flex-col gap-0.5">
                    <span className="font-semibold text-ink-900">{domain.name}</span>
                    {domain.description && (
                      <span className="line-clamp-1 text-[13px] text-ink-500">{domain.description}</span>
                    )}
                  </div>
                </div>
              </td>
              <td className="px-3 whitespace-nowrap text-ink-700">{domain.createdByName ?? '—'}</td>
              <td className="px-3">
                <CatalogStatusBadge status={domain.status} />
              </td>
              <td className="px-3 text-[13px] text-ink-700">{domain.createdAt ? formatDate(domain.createdAt) : '—'}</td>
              <td className="px-4">
                <div className="flex justify-center">
                  <EditButton domain={domain} onEdit={onEdit} />
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Mobile */}
      <ul className="flex flex-col md:hidden">
        {domains.map((domain) => (
          <li key={domain.id} className="flex gap-3 border-t border-line-soft px-4 py-4 first:border-t-0">
            <DomainBadge name={domain.name} />
            <div className="flex min-w-0 flex-1 flex-col gap-1.5">
              <div className="flex items-start justify-between gap-2">
                <span className="font-semibold text-ink-900">{domain.name}</span>
                <EditButton domain={domain} onEdit={onEdit} />
              </div>
              {domain.description && <p className="line-clamp-2 text-[13px] text-ink-500">{domain.description}</p>}
              <div className="flex flex-wrap items-center gap-x-3 gap-y-1.5 text-[13px] text-ink-600">
                <CatalogStatusBadge status={domain.status} />
                <span>{domain.createdByName ?? '—'}</span>
                {domain.createdAt && <span>{formatDate(domain.createdAt)}</span>}
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
