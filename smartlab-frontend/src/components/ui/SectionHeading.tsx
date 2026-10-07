import type { ReactNode } from 'react'
import { ChevronRightIcon } from '../icons'
import { ButtonLink } from './Button'

interface SectionHeadingProps {
  eyebrow: string
  title: string
  description?: ReactNode
  action?: { label: string; to: string }
}

/** Tiêu đề section: nhãn nhỏ + tiêu đề lớn + nút "Tất cả ..." bên phải. */
export function SectionHeading({ eyebrow, title, description, action }: SectionHeadingProps) {
  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between sm:gap-6">
        <div className="flex flex-col gap-2">
          <span className="text-sm font-semibold text-brand-600">{eyebrow}</span>
          <h2 className="text-[26px] font-bold leading-tight tracking-[-0.01em] sm:text-3xl lg:text-[34px]">{title}</h2>
        </div>
        {action && (
          <ButtonLink
            to={action.to}
            variant="outline"
            className="self-start sm:self-auto"
            rightIcon={<ChevronRightIcon />}
          >
            {action.label}
          </ButtonLink>
        )}
      </div>
      {description && <p className="max-w-[720px] text-base leading-relaxed text-ink-600">{description}</p>}
    </div>
  )
}
