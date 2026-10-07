import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { AlertCircleIcon, EyeIcon, EyeOffIcon, LockIcon, MailIcon } from '../../../components/icons'
import { Button } from '../../../components/ui/Button'
import { TextField } from '../../../components/ui/TextField'
import { getPostLoginPath } from '../../../constants/roles'
import { ROUTES } from '../../../constants/routes'
import { useAuth } from '../hooks/useAuth'
import { AuthError } from '../../../services/authService'

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

interface FieldErrors {
  email?: string
  password?: string
}

function validate(email: string, password: string): FieldErrors {
  const errors: FieldErrors = {}
  if (!email.trim()) errors.email = 'Vui lòng nhập email.'
  else if (!EMAIL_PATTERN.test(email.trim())) errors.email = 'Email không hợp lệ.'
  if (!password) errors.password = 'Vui lòng nhập mật khẩu.'
  return errors
}

export function LoginForm() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: string } | null)?.from

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(true)
  const [showPassword, setShowPassword] = useState(false)
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [formError, setFormError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFormError(null)

    const errors = validate(email, password)
    setFieldErrors(errors)
    if (errors.email || errors.password) return

    setSubmitting(true)
    try {
      const user = await login({ email: email.trim(), password, rememberMe })
      navigate(getPostLoginPath(user.role, from), { replace: true })
    } catch (error) {
      setFormError(error instanceof AuthError ? error.message : 'Không thể đăng nhập, vui lòng thử lại sau.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="flex flex-col gap-7">
      <div className="flex flex-col gap-2">
        <h2 className="text-[28px] leading-tight font-bold tracking-[-0.01em] sm:text-[32px]">Đăng nhập</h2>
        <p className="text-[15px] text-ink-600">Dùng email tổ chức do IT Admin cấp để vào hệ thống SmartLab.</p>
      </div>

      {formError && (
        <div role="alert" className="flex items-start gap-2.5 rounded-lg border border-[#f0c9c4] bg-[#fbecea] px-4 py-3 text-sm text-[#8a2c22]">
          <AlertCircleIcon size={18} className="mt-px shrink-0" />
          {formError}
        </div>
      )}

      <form noValidate onSubmit={handleSubmit} className="flex flex-col gap-5">
        <TextField
          label="Email tổ chức"
          type="email"
          autoComplete="username"
          placeholder="ten@smartlab.vn"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          error={fieldErrors.email}
          leftIcon={<MailIcon size={18} />}
          autoFocus
        />

        <TextField
          label="Mật khẩu"
          type={showPassword ? 'text' : 'password'}
          autoComplete="current-password"
          placeholder="Nhập mật khẩu"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          error={fieldErrors.password}
          leftIcon={<LockIcon size={18} />}
          rightSlot={
            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
              aria-label={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
              className="shrink-0 rounded p-1 text-ink-500 hover:text-brand-600"
            >
              {showPassword ? <EyeOffIcon size={18} /> : <EyeIcon size={18} />}
            </button>
          }
        />

        <div className="flex items-center justify-between gap-4 text-sm">
          <label className="inline-flex cursor-pointer items-center gap-2 text-ink-700">
            <input
              type="checkbox"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
              className="size-4"
            />
            Ghi nhớ đăng nhập
          </label>
          <Link to={ROUTES.FORGOT_PASSWORD} className="font-semibold text-brand-600 hover:text-brand-700">
            Quên mật khẩu?
          </Link>
        </div>

        <Button type="submit" size="lg" disabled={submitting} className="w-full">
          {submitting && (
            <span className="size-4 animate-spin rounded-full border-2 border-white/40 border-t-white" aria-hidden="true" />
          )}
          {submitting ? 'Đang đăng nhập…' : 'Đăng nhập'}
        </Button>
      </form>

      <p className="text-center text-sm text-ink-600">
        Chưa là thành viên Lab?{' '}
        <Link to={ROUTES.RECRUITMENT} className="font-semibold text-brand-600 hover:text-brand-700">
          Xem bài tuyển & ứng tuyển
        </Link>
      </p>

    </div>
  )
}
