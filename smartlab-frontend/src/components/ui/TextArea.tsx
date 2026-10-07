import { useId } from 'react'
import type { TextareaHTMLAttributes } from 'react'
import { cn } from '../../utils/format'

interface TextAreaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label: string
  error?: string
  hint?: string
}

export function TextArea({ label, error, hint, className, id, ...rest }: TextAreaProps) {
  const autoId = useId()
  const inputId = id ?? autoId
  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={inputId} className="text-sm font-medium text-ink-700">
        {label}
      </label>
      <textarea
        id={inputId}
        aria-invalid={error ? true : undefined}
        className={cn(
          'min-h-28 resize-y rounded-lg border bg-white px-3.5 py-3 text-[15px] text-ink-900 outline-none placeholder:text-ink-300',
          'focus:border-brand-600 focus:ring-3 focus:ring-brand-600/15',
          error ? 'border-[#c2473a]' : 'border-line-strong',
          className,
        )}
        {...rest}
      />
      {error ? (
        <span className="text-[13px] text-[#c2473a]">{error}</span>
      ) : (
        hint && <span className="text-[13px] text-ink-500">{hint}</span>
      )}
    </div>
  )
}
