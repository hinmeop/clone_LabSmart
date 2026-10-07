import { useState } from 'react'
import type { FormEvent } from 'react'
import { TrashIcon } from '../../../components/icons'
import { Button } from '../../../components/ui/Button'
import { Modal } from '../../../components/ui/Modal'
import { TextArea } from '../../../components/ui/TextArea'
import { TextField } from '../../../components/ui/TextField'
import { ApiError, getErrorMessage } from '../../../services/axiosClient'
import { domainService } from '../../../services/domainService'
import type { Domain } from '../../../types/domain.types'
import { formatDate } from '../../../utils/format'

const NAME_MAX = 255
const DESCRIPTION_MAX = 2000

interface DomainFormModalProps {
  /** null = thêm mới, có giá trị = cập nhật */
  domain: Domain | null
  open: boolean
  onClose: () => void
  onSaved: (domain: Domain, mode: 'create' | 'update') => void
  onRequestDelete: (domain: Domain) => void
}

interface FormErrors {
  name?: string
  description?: string
  form?: string
}

/** Popup thêm domain (PM6) / cập nhật domain (PM6b). */
export function DomainFormModal({ domain, open, onClose, onSaved, onRequestDelete }: DomainFormModalProps) {
  const isEdit = domain !== null
  const [name, setName] = useState(domain?.name ?? '')
  const [description, setDescription] = useState(domain?.description ?? '')
  const [errors, setErrors] = useState<FormErrors>({})
  const [saving, setSaving] = useState(false)

  function validate(): FormErrors {
    const e: FormErrors = {}
    if (!name.trim()) e.name = 'Vui lòng nhập tên domain.'
    else if (name.trim().length > NAME_MAX) e.name = `Tên domain tối đa ${NAME_MAX} ký tự.`
    if (description.length > DESCRIPTION_MAX) e.description = `Mô tả tối đa ${DESCRIPTION_MAX} ký tự.`
    return e
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const e = validate()
    setErrors(e)
    if (e.name || e.description) return

    setSaving(true)
    const payload = { name: name.trim(), description: description.trim() || null }
    try {
      const saved = isEdit ? await domainService.update(domain.id, payload) : await domainService.create(payload)
      onSaved(saved, isEdit ? 'update' : 'create')
    } catch (err) {
      if (err instanceof ApiError && err.status === 409) {
        setErrors({ name: 'Tên domain đã tồn tại.' })
      } else if (err instanceof ApiError && err.status === 400 && err.fieldErrors) {
        setErrors({
          name: err.fieldErrors.name ? 'Tên domain không hợp lệ (bắt buộc, tối đa 255 ký tự).' : undefined,
          description: err.fieldErrors.description ? 'Mô tả tối đa 2000 ký tự.' : undefined,
        })
      } else {
        setErrors({ form: getErrorMessage(err, isEdit ? 'Không lưu được thay đổi.' : 'Không tạo được domain.') })
      }
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      busy={saving}
      title={isEdit ? 'Cập nhật domain' : 'Thêm domain'}
      subtitle={isEdit ? domain.name : 'Domain mới sẽ hiển thị trong danh sách domain.'}
      footer={
        <>
          {isEdit && (
            <Button
              variant="danger-outline"
              leftIcon={<TrashIcon size={16} />}
              className="sm:mr-auto"
              disabled={saving}
              onClick={() => onRequestDelete(domain)}
            >
              Xóa domain
            </Button>
          )}
          <Button variant="outline" onClick={onClose} disabled={saving}>
            Hủy
          </Button>
          <Button type="submit" form="domain-form" disabled={saving}>
            {saving ? 'Đang lưu…' : isEdit ? 'Lưu thay đổi' : 'Tạo domain'}
          </Button>
        </>
      }
    >
      <form id="domain-form" noValidate onSubmit={handleSubmit} className="flex flex-col gap-4">
        {errors.form && (
          <div role="alert" className="rounded-lg border border-[#f0c9c4] bg-[#fbecea] px-4 py-3 text-sm text-[#8a2c22]">
            {errors.form}
          </div>
        )}

        <TextField
          label="Tên domain *"
          placeholder="VD: Nghiên cứu khoa học"
          value={name}
          maxLength={NAME_MAX}
          onChange={(e) => setName(e.target.value)}
          error={errors.name}
          autoFocus
        />

        <TextArea
          label="Mô tả"
          placeholder="Phạm vi, lĩnh vực của domain…"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          error={errors.description}
          hint={`${description.length}/${DESCRIPTION_MAX} ký tự`}
        />

        {isEdit && (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <TextField label="Người tạo" value={domain.createdByName ?? '—'} readOnly tabIndex={-1} className="text-ink-600" />
            <TextField
              label="Ngày tạo"
              value={domain.createdAt ? formatDate(domain.createdAt) : '—'}
              readOnly
              tabIndex={-1}
              className="text-ink-600"
            />
          </div>
        )}
      </form>
    </Modal>
  )
}
