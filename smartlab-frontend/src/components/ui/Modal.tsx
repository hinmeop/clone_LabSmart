import { useEffect, useId } from 'react'
import type { ReactNode } from 'react'
import { createPortal } from 'react-dom'
import { cn } from '../../utils/format'
import { CloseIcon } from '../icons'

interface ModalProps {
  open: boolean
  onClose: () => void
  title: string
  subtitle?: ReactNode
  children: ReactNode
  footer?: ReactNode
  /** Độ rộng tối đa: sm ≈ 440px, md ≈ 560px */
  size?: 'sm' | 'md'
  /** Chặn đóng khi đang gửi request */
  busy?: boolean
}

export function Modal({ open, onClose, title, subtitle, children, footer, size = 'md', busy = false }: ModalProps) {
  const titleId = useId()

  useEffect(() => {
    if (!open) return
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !busy) onClose()
    }
    document.addEventListener('keydown', onKey)
    const prevOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    return () => {
      document.removeEventListener('keydown', onKey)
      document.body.style.overflow = prevOverflow
    }
  }, [open, busy, onClose])

  if (!open) return null

  return createPortal(
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-ink-900/45 p-0 sm:items-center sm:p-4">
      <button
        type="button"
        aria-label="Đóng"
        tabIndex={-1}
        className="absolute inset-0 cursor-default"
        onClick={() => !busy && onClose()}
      />
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        className={cn(
          'relative flex max-h-[92vh] w-full flex-col overflow-hidden rounded-t-2xl bg-white shadow-xl sm:rounded-2xl',
          size === 'sm' ? 'sm:max-w-[440px]' : 'sm:max-w-[560px]',
        )}
      >
        <div className="flex items-start gap-4 border-b border-line px-5 py-4 sm:px-6">
          <div className="flex min-w-0 flex-1 flex-col gap-1">
            <h2 id={titleId} className="text-lg font-semibold">
              {title}
            </h2>
            {subtitle && <p className="truncate text-sm text-ink-500">{subtitle}</p>}
          </div>
          <button
            type="button"
            aria-label="Đóng"
            disabled={busy}
            onClick={onClose}
            className="-mr-1 flex size-9 shrink-0 items-center justify-center rounded-lg text-ink-500 hover:bg-canvas hover:text-ink-900"
          >
            <CloseIcon size={18} />
          </button>
        </div>
        <div className="overflow-y-auto px-5 py-5 sm:px-6">{children}</div>
        {footer && (
          <div className="flex flex-col-reverse gap-2 border-t border-line bg-[#f8f9f7] px-5 py-4 sm:flex-row sm:justify-end sm:px-6">
            {footer}
          </div>
        )}
      </div>
    </div>,
    document.body,
  )
}
