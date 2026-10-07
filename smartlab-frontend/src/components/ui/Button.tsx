import type { ButtonHTMLAttributes, ReactNode } from 'react'
import { Link } from 'react-router-dom'
import type { LinkProps } from 'react-router-dom'
import { cn } from '../../utils/format'

type ButtonVariant = 'primary' | 'outline' | 'white' | 'accent' | 'danger' | 'danger-outline'
type ButtonSize = 'sm' | 'md' | 'lg'

const VARIANT_CLASSES: Record<ButtonVariant, string> = {
  primary: 'bg-brand-600 text-white hover:bg-brand-700',
  outline: 'border border-line-strong bg-white text-ink-900 hover:border-brand-600 hover:text-brand-600',
  white: 'bg-white text-brand-600 hover:bg-brand-50',
  accent: 'bg-brand-300 text-brand-900 hover:bg-[#a3dacc]',
  danger: 'bg-[#b3261e] text-white hover:bg-[#8f1e18]',
  'danger-outline': 'border border-[#e8c3bf] bg-white text-[#b3261e] hover:bg-[#fbecea]',
}

const SIZE_CLASSES: Record<ButtonSize, string> = {
  sm: 'h-9 px-3.5 text-sm',
  md: 'h-11 px-4 text-sm sm:px-[18px] sm:text-[15px]',
  lg: 'h-12 px-5 text-[15px]',
}

export function buttonClasses(variant: ButtonVariant = 'primary', size: ButtonSize = 'md', className?: string) {
  return cn(
    'inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-lg font-semibold transition-colors',
    'focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600',
    'disabled:cursor-not-allowed disabled:opacity-60',
    VARIANT_CLASSES[variant],
    SIZE_CLASSES[size],
    className,
  )
}

interface CommonProps {
  variant?: ButtonVariant
  size?: ButtonSize
  leftIcon?: ReactNode
  rightIcon?: ReactNode
}

type ButtonProps = CommonProps & ButtonHTMLAttributes<HTMLButtonElement>

export function Button({ variant, size, leftIcon, rightIcon, className, children, type = 'button', ...rest }: ButtonProps) {
  return (
    <button type={type} className={buttonClasses(variant, size, className)} {...rest}>
      {leftIcon}
      {children}
      {rightIcon}
    </button>
  )
}

type ButtonLinkProps = CommonProps & LinkProps

/** Nút có hình thức button nhưng điều hướng bằng React Router. */
export function ButtonLink({ variant, size, leftIcon, rightIcon, className, children, ...rest }: ButtonLinkProps) {
  return (
    <Link className={buttonClasses(variant, size, className)} {...rest}>
      {leftIcon}
      {children}
      {rightIcon}
    </Link>
  )
}
