import type { Tone } from '../../types/common.types'
import { cn, getInitials } from '../../utils/format'
import { TONE_BG } from '../../utils/tone'

type AvatarSize = 'sm' | 'md' | 'lg'

const SIZE_CLASSES: Record<AvatarSize, string> = {
  sm: 'size-7 text-[11px]',
  md: 'size-9 text-[13px]',
  lg: 'size-16 text-xl',
}

interface AvatarProps {
  name: string
  src?: string
  tone?: Tone
  size?: AvatarSize
  className?: string
}

/** Avatar tròn: hiển thị ảnh nếu có, ngược lại hiển thị chữ cái đầu trên nền pastel. */
export function Avatar({ name, src, tone = 'mint', size = 'sm', className }: AvatarProps) {
  const base = cn('shrink-0 rounded-full', SIZE_CLASSES[size], className)

  if (src) {
    return <img src={src} alt={name} className={cn(base, 'object-cover')} loading="lazy" />
  }

  return (
    <span
      aria-hidden="true"
      className={cn(base, TONE_BG[tone], 'flex items-center justify-center font-semibold text-ink-900')}
    >
      {getInitials(name)}
    </span>
  )
}
