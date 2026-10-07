import { useCallback, useState } from 'react'
import { Link } from 'react-router-dom'
import { ChevronRightIcon, PlusIcon, RotateIcon, SearchIcon } from '../../components/icons'
import { Button } from '../../components/ui/Button'
import { Pagination } from '../../components/ui/Pagination'
import { Toast } from '../../components/ui/Toast'
import type { ToastMessage } from '../../components/ui/Toast'
import { ROUTES } from '../../constants/routes'
import { DeleteDomainModal, DomainFormModal, DomainTable, useDomainList } from '../../features/domain'
import type { Domain } from '../../types/domain.types'

type ModalState =
  | { type: 'none' }
  | { type: 'create' }
  | { type: 'edit'; domain: Domain }
  | { type: 'delete'; domain: Domain }

/** PM1 · Danh sách domain */
export default function DomainListPage() {
  const list = useDomainList()
  const [modal, setModal] = useState<ModalState>({ type: 'none' })
  const [toast, setToast] = useState<ToastMessage | null>(null)

  const notify = (text: string, type: ToastMessage['type'] = 'success') => setToast({ id: Date.now(), type, text })
  const closeModal = () => setModal({ type: 'none' })
  const closeToast = useCallback(() => setToast(null), [])

  return (
    <div className="mx-auto flex w-full max-w-[1200px] flex-col gap-5">
      {/* Breadcrumb */}
      <nav aria-label="Breadcrumb" className="flex items-center gap-2 text-[13px] text-ink-500">
        <Link to={ROUTES.WORKSPACE} className="text-ink-500 hover:text-brand-600">
          Dự án
        </Link>
        <ChevronRightIcon size={14} className="text-ink-300" />
        <span aria-current="page" className="font-medium text-ink-900">
          Domain
        </span>
      </nav>

      {/* Tiêu đề */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div className="flex flex-col gap-1.5">
          <h1 className="text-2xl font-medium tracking-[-0.015em] sm:text-[30px]">Domain</h1>
          <p className="text-[15px] text-ink-600">Chọn domain để duyệt dự án theo năm → kỳ học → dự án.</p>
        </div>
        <Button leftIcon={<PlusIcon />} onClick={() => setModal({ type: 'create' })} className="self-start sm:self-auto">
          Thêm domain
        </Button>
      </div>

      {/* Bộ lọc */}
      <form
        role="search"
        onSubmit={(e) => e.preventDefault()}
        onReset={(e) => {
          e.preventDefault()
          list.reset()
        }}
        className="flex flex-col gap-3 rounded-xl border border-line bg-white p-4 sm:flex-row sm:items-end"
      >
        <label className="flex flex-1 flex-col gap-1.5">
          <span className="text-[13px] font-semibold text-ink-700">Tìm kiếm</span>
          <span className="flex h-[42px] items-center gap-2 rounded-lg border border-[#d5dad5] px-3 text-ink-500 focus-within:border-brand-600 focus-within:ring-3 focus-within:ring-brand-600/15">
            <SearchIcon size={17} />
            <input
              type="search"
              placeholder="Tên domain…"
              value={list.searchInput}
              onChange={(e) => list.setSearchInput(e.target.value)}
              className="h-full min-w-0 flex-1 bg-transparent text-sm text-ink-900 outline-none"
            />
          </span>
        </label>
        <Button type="reset" leftIcon={<RotateIcon size={16} />} className="h-[42px]">
          Đặt lại
        </Button>
      </form>

      {/* Danh sách */}
      <section className="overflow-hidden rounded-xl border border-line bg-white">
        {list.error ? (
          <div className="flex flex-col items-center gap-3 px-4 py-14 text-center">
            <p className="text-sm text-[#b3261e]">{list.error}</p>
            <Button variant="outline" size="sm" onClick={list.reload}>
              Thử lại
            </Button>
          </div>
        ) : (
          <>
            <DomainTable
              domains={list.data.items}
              loading={list.loading}
              onEdit={(domain) => setModal({ type: 'edit', domain })}
            />
            {list.data.totalElements > 0 && (
              <Pagination
                page={list.data.page}
                size={list.size}
                totalElements={list.data.totalElements}
                totalPages={list.data.totalPages}
                itemLabel="domain"
                onPageChange={list.setPage}
                onSizeChange={list.setSize}
              />
            )}
          </>
        )}
      </section>

      {/* Popup thêm / sửa — key để reset form mỗi lần mở */}
      {(modal.type === 'create' || modal.type === 'edit') && (
        <DomainFormModal
          key={modal.type === 'edit' ? modal.domain.id : 'new'}
          open
          domain={modal.type === 'edit' ? modal.domain : null}
          onClose={closeModal}
          onSaved={(saved, mode) => {
            closeModal()
            notify(mode === 'create' ? `Đã tạo domain “${saved.name}”.` : `Đã cập nhật domain “${saved.name}”.`)
            list.reload()
          }}
          onRequestDelete={(domain) => setModal({ type: 'delete', domain })}
        />
      )}

      {modal.type === 'delete' && (
        <DeleteDomainModal
          open
          domain={modal.domain}
          onClose={() => setModal({ type: 'edit', domain: modal.domain })}
          onDeleted={(deleted) => {
            closeModal()
            notify(`Đã xóa domain “${deleted.name}”.`)
            list.reload()
          }}
        />
      )}

      <Toast toast={toast} onClose={closeToast} />
    </div>
  )
}
