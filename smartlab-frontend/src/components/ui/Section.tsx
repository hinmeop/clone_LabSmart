import type { ReactNode } from 'react'
import { cn } from '../../utils/format'
import { Container } from './Container'

interface SectionProps {
  id?: string
  children: ReactNode
  /** `white`: nền trắng có viền trên/dưới; `canvas`: nền xám nhạt mặc định. */
  background?: 'white' | 'canvas'
  className?: string
}

export function Section({ id, children, background = 'canvas', className }: SectionProps) {
  return (
    <section
      id={id}
      className={cn(
        'py-14 sm:py-16 lg:py-20',
        background === 'white' && 'border-y border-line bg-white',
        className,
      )}
    >
      <Container className="flex flex-col gap-6 sm:gap-8">{children}</Container>
    </section>
  )
}
