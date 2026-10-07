import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { MailIcon } from '../components/icons'
import { Button } from '../components/ui/Button'
import { TextField } from '../components/ui/TextField'
import { ROUTES } from '../constants/routes'
import { AuthLayout } from '../features/auth'

/**
 * Quên mật khẩu — bước 1: nhập email để nhận mã OTP (auth_verification_codes, purpose = password_reset).
 * TODO: nối API POST /api/auth/forgot-password và thêm bước nhập OTP + mật khẩu mới.
 */
export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [error, setError] = useState<string>()
  const [sent, setSent] = useState(false)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      setError('Vui lòng nhập email tổ chức hợp lệ.')
      return
    }
    setError(undefined)
    setSent(true)
  }

  return (
    <AuthLayout>
      <div className="flex flex-col gap-7">
        <div className="flex flex-col gap-2">
          <h2 className="text-[28px] leading-tight font-bold sm:text-[32px]">Quên mật khẩu</h2>
          <p className="text-[15px] text-ink-600">Nhập email tổ chức, chúng tôi sẽ gửi mã xác nhận để đặt lại mật khẩu.</p>
        </div>

        {sent ? (
          <div role="status" className="rounded-lg border border-brand-100 bg-brand-50 px-4 py-3 text-sm text-brand-700">
            Nếu email <strong>{email.trim()}</strong> tồn tại trong hệ thống, mã xác nhận đã được gửi. Vui lòng kiểm tra hộp thư.
          </div>
        ) : (
          <form noValidate onSubmit={handleSubmit} className="flex flex-col gap-5">
            <TextField
              label="Email tổ chức"
              type="email"
              placeholder="ten@fpt.edu.vn"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={error}
              leftIcon={<MailIcon size={18} />}
              autoFocus
            />
            <Button type="submit" size="lg" className="w-full">
              Gửi mã xác nhận
            </Button>
          </form>
        )}

        <Link to={ROUTES.LOGIN} className="text-center text-sm font-semibold text-brand-600 hover:text-brand-700">
          Quay lại đăng nhập
        </Link>
      </div>
    </AuthLayout>
  )
}
