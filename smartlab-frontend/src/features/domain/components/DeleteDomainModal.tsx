import { useState } from 'react'
import { Button } from '../../../components/ui/Button'
import { Modal } from '../../../components/ui/Modal'
import { TextField } from '../../../components/ui/TextField'
import { getErrorMessage } from '../../../services/axiosClient'
import { domainService } from '../../../services/domainService'
import type { Domain } from '../../../types/domain.types'

interface DeleteDomainModalProps {
  domain: Domain
  open: boolean
  onClose: () => void
  onDeleted: (domain: Domain) => void
}

/** Popup xóa domain — gõ đúng tên để xác nhận (PM7). */
export function DeleteDomainModal({ domain, open, onClose, onDeleted }: DeleteDomainModalProps) {
  const [confirmText, setConfirmText] = useState('')
  const [deleting, setDeleting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const matched = confirmText.trim() === domain.name

  async function handleDelete() {
    setDeleting(true)
    setError(null)
    try {
      await domainService.remove(domain.id)
      onDeleted(domain)
    } catch (err) {
      setError(getErrorMessage(err, 'Không xóa được domain.'))
    } finally {
      setDeleting(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      busy={deleting}
      size="sm"
      title="Xóa domain?"
      subtitle={domain.name}
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={deleting}>
            Hủy
          </Button>
          <Button variant="danger" onClick={handleDelete} disabled={!matched || deleting}>
            {deleting ? 'Đang xóa…' : 'Xóa domain'}
          </Button>
        </>
      }
    >
      <div className="flex flex-col gap-4">
        <p className="rounded-lg bg-[#fbecea] px-4 py-3 text-sm text-[#8a2c22]">
          Domain sẽ bị gỡ khỏi danh sách cùng cấu trúc năm / kỳ bên trong. Thao tác không thể hoàn tác từ giao diện.
        </p>
        {error && (
          <div role="alert" className="text-sm text-[#b3261e]">
            {error}
          </div>
        )}
        <div className="flex flex-col gap-1.5">
          <TextField
            label="Nhập tên domain để xác nhận *"
            value={confirmText}
            onChange={(e) => setConfirmText(e.target.value)}
            autoFocus
          />
          <span className="text-[13px] text-ink-500">
            Gõ chính xác <strong className="text-ink-900">{domain.name}</strong> để bật nút Xóa.
          </span>
        </div>
      </div>
    </Modal>
  )
}
