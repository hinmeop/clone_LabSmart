import { forwardRef, useId } from 'react'
import type { InputHTMLAttributes, ReactNode } from 'react'
import { cn } from '../../utils/format'

interface TextFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
  error?: string
  leftIcon?: ReactNode
  /** Phần tử bên phải ô nhập, VD nút ẩn/hiện mật khẩu. */
  rightSlot?: ReactNode
}

export const TextField = forwardRef<HTMLInputElement, TextFieldProps>(function TextField(
  { label, error, leftIcon, rightSlot, className, id, ...rest },
  ref,
) {
  const autoId = useId()
  const inputId = id ?? autoId
  const errorId = `${inputId}-error`

  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={inputId} className="text-sm font-medium text-ink-700">
        {label}
      </label>
      <div
        className={cn(
          'flex h-12 items-center gap-2.5 rounded-lg border bg-white px-3.5 transition-colors',
          'focus-within:border-brand-600 focus-within:ring-3 focus-within:ring-brand-600/15',
          error ? 'border-[#c2473a]' : 'border-line-strong',
          rest.readOnly && 'border-line bg-[#f8f9f7] focus-within:border-line focus-within:ring-0',
        )}
      >
        {leftIcon && <span className="shrink-0 text-ink-500">{leftIcon}</span>}
        <input
          ref={ref}
          id={inputId}
          aria-invalid={error ? true : undefined}
          aria-describedby={error ? errorId : undefined}
          className={cn(
            'h-full min-w-0 flex-1 bg-transparent text-[15px] text-ink-900 outline-none placeholder:text-ink-300',
            className,
          )}
          {...rest}
        />
        {rightSlot}
      </div>
      {error && (
        <span id={errorId} className="text-[13px] text-[#c2473a]">
          {error}
        </span>
      )}
    </div>
  )
})
