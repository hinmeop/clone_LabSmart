import { Outlet } from 'react-router-dom'
import { PublicFooter } from '../components/layout/PublicFooter'
import { PublicHeader } from '../components/layout/PublicHeader'

/** Layout cho các trang công khai (Guest): header + nội dung + footer liên hệ. */
export default function PublicLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-canvas">
      <PublicHeader />
      <main className="flex-1">
        <Outlet />
      </main>
      <PublicFooter />
    </div>
  )
}
