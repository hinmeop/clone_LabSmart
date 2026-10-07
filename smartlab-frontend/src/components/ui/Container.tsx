import type { ReactNode } from 'react'
import { cn } from '../../utils/format'

interface ContainerProps {
  children: ReactNode
  className?: string
}

/** Khung nội dung: tối đa 1440px, lề 16px (mobile) → 32px (tablet) → 80px (desktop). */
export function Container({ children, className }: ContainerProps) {
  return <div className={cn('mx-auto w-full max-w-[1440px] px-4 sm:px-8 lg:px-20', className)}>{children}</div>
}
