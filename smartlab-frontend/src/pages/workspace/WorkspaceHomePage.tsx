import { ROLE_LABELS } from '../../constants/roles'
import { useAuth } from '../../features/auth'

/** Dashboard workspace — tạm thời, sẽ thay bằng dashboard thật. */
export default function WorkspaceHomePage() {
  const { user } = useAuth()
  if (!user) return null
  return (
    <div className="mx-auto flex w-full max-w-[1200px] flex-col gap-2">
      <h1 className="text-2xl font-medium sm:text-[30px]">Xin chào, {user.fullName}</h1>
      <p className="text-ink-600">
        Vai trò <strong>{ROLE_LABELS[user.role]}</strong> · Chọn chức năng ở menu bên trái để bắt đầu.
      </p>
    </div>
  )
}
