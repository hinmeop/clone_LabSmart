import { cn } from '../../utils/format'
import { ChevronLeftIcon, ChevronRightIcon } from '../icons'

interface PaginationProps {
  /** Trang hiện tại, bắt đầu từ 0 (giống Spring) */
  page: number
  size: number
  totalElements: number
  totalPages: number
  itemLabel: string
  sizeOptions?: number[]
  onPageChange: (page: number) => void
  onSizeChange: (size: number) => void
}

/** Rút gọn dãy số trang: 1 … 4 5 6 … 10 */
function pageNumbers(current: number, total: number): Array<number | 'gap'> {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i)
  const pages = new Set([0, total - 1, current - 1, current, current + 1])
  const sorted = [...pages].filter((p) => p >= 0 && p < total).sort((a, b) => a - b)
  const result: Array<number | 'gap'> = []
  sorted.forEach((p, i) => {
    if (i > 0 && p - sorted[i - 1] > 1) result.push('gap')
    result.push(p)
  })
  return result
}

export function Pagination({
  page,
  size,
  totalElements,
  totalPages,
  itemLabel,
  sizeOptions = [10, 25, 50],
  onPageChange,
  onSizeChange,
}: PaginationProps) {
  const from = totalElements === 0 ? 0 : page * size + 1
  const to = Math.min((page + 1) * size, totalElements)
  const navBtn =
    'flex size-9 items-center justify-center rounded-lg border border-line-strong bg-white text-ink-900 disabled:cursor-not-allowed disabled:border-line disabled:text-ink-300'

  return (
    <div className="flex flex-col gap-3 border-t border-line px-4 py-3 text-sm text-ink-600 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex flex-wrap items-center gap-x-4 gap-y-2">
        <span>
          Hiển thị{' '}
          <strong className="text-ink-900">
            {from}–{to}
          </strong>{' '}
          trong <strong className="text-ink-900">{totalElements}</strong> {itemLabel}
        </span>
        <label className="flex items-center gap-2">
          Số dòng / trang
          <select
            value={size}
            onChange={(e) => onSizeChange(Number(e.target.value))}
            className="h-9 rounded-lg border border-line-strong bg-white px-2.5 font-medium text-ink-900"
          >
            {sizeOptions.map((o) => (
              <option key={o} value={o}>
                {o}
              </option>
            ))}
          </select>
        </label>
      </div>

      <nav aria-label="Phân trang" className="flex items-center gap-1 self-end sm:self-auto">
        <button type="button" aria-label="Trang trước" disabled={page <= 0} onClick={() => onPageChange(page - 1)} className={navBtn}>
          <ChevronLeftIcon />
        </button>
        {pageNumbers(page, Math.max(totalPages, 1)).map((p, i) =>
          p === 'gap' ? (
            <span key={`gap-${i}`} className="px-1 text-ink-300">
              …
            </span>
          ) : (
            <button
              key={p}
              type="button"
              aria-current={p === page ? 'page' : undefined}
              onClick={() => onPageChange(p)}
              className={cn(
                'size-9 rounded-lg font-medium',
                p === page ? 'bg-brand-600 text-white' : 'text-ink-900 hover:bg-canvas',
              )}
            >
              {p + 1}
            </button>
          ),
        )}
        <button
          type="button"
          aria-label="Trang sau"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
          className={navBtn}
        >
          <ChevronRightIcon />
        </button>
      </nav>
    </div>
  )
}
