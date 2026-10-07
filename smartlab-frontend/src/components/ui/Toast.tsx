import { useEffect } from 'react'
import { createPortal } from 'react-dom'
import { cn } from '../../utils/format'
import { AlertCircleIcon, CloseIcon, ShieldIcon } from '../icons'

export interface ToastMessage {
  id: number
  type: 'success' | 'error'
  text: string
}

/** Thông báo nhỏ góc dưới màn hình, tự ẩn sau 3,5 giây. */
export function Toast({ toast, onClose }: { toast: ToastMessage | null; onClose: () => void }) {
  useEffect(() => {
    if (!toast) return
    const t = setTimeout(onClose, 3500)
    return () => clearTimeout(t)
  }, [toast, onClose])

  if (!toast) return null
  const success = toast.type === 'success'

  return createPortal(
    <div
      role="status"
      className={cn(
        'fixed right-4 bottom-4 left-4 z-[60] flex items-start gap-3 rounded-xl border px-4 py-3 text-sm shadow-lg sm:left-auto sm:w-[380px]',
        success ? 'border-brand-100 bg-white text-ink-900' : 'border-[#f0c9c4] bg-[#fbecea] text-[#8a2c22]',
      )}
    >
      {success ? (
        <ShieldIcon size={18} className="mt-px shrink-0 text-brand-600" />
      ) : (
        <AlertCircleIcon size={18} className="mt-px shrink-0" />
      )}
      <span className="flex-1">{toast.text}</span>
      <button type="button" aria-label="Đóng thông báo" onClick={onClose} className="text-ink-500 hover:text-ink-900">
        <CloseIcon size={16} />
      </button>
    </div>,
    document.body,
  )
}
