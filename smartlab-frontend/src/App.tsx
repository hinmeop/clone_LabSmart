import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { AREA_ROLES } from './constants/roles'
import { ROUTES } from './constants/routes'
import { AuthProvider } from './features/auth'
import PublicLayout from './layouts/PublicLayout'
import WorkspaceLayout from './layouts/WorkspaceLayout'
import ForbiddenPage from './pages/ForbiddenPage'
import ForgotPasswordPage from './pages/ForgotPasswordPage'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import NotFoundPage from './pages/NotFoundPage'
import PortalHomePage from './pages/PortalHomePage'
import TestPage from './pages/TestPage'
import ComingSoonPage from './pages/workspace/ComingSoonPage'
import DomainListPage from './pages/workspace/DomainListPage'
import WorkspaceHomePage from './pages/workspace/WorkspaceHomePage'
import { GuestOnlyRoute } from './routes/GuestOnlyRoute'
import { ProtectedRoute } from './routes/ProtectedRoute'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Khu vực công khai (Guest) */}
          <Route element={<PublicLayout />}>
            <Route path={ROUTES.HOME} element={<HomePage />} />
            <Route path="*" element={<NotFoundPage />} />
          </Route>

          {/* Xác thực — chỉ cho người chưa đăng nhập */}
          <Route element={<GuestOnlyRoute />}>
            <Route path={ROUTES.LOGIN} element={<LoginPage />} />
            <Route path={ROUTES.FORGOT_PASSWORD} element={<ForgotPasswordPage />} />
          </Route>

          {/* Khu vực nội bộ — chặn theo role */}
          <Route element={<ProtectedRoute allowedRoles={AREA_ROLES.ADMIN} />}>
            <Route path={`${ROUTES.ADMIN}/*`} element={<PortalHomePage area="IT Admin" />} />
          </Route>
          <Route element={<ProtectedRoute allowedRoles={AREA_ROLES.BUSINESS} />}>
            <Route path={`${ROUTES.BUSINESS}/*`} element={<PortalHomePage area="Business Admin" />} />
          </Route>
          <Route element={<ProtectedRoute allowedRoles={AREA_ROLES.WORKSPACE} />}>
            <Route path={ROUTES.WORKSPACE} element={<WorkspaceLayout />}>
              <Route index element={<WorkspaceHomePage />} />
              {/* Domain & Dự án — chỉ Manager */}
              <Route element={<ProtectedRoute allowedRoles={['manager']} />}>
                <Route path={ROUTES.WORKSPACE_DOMAINS} element={<DomainListPage />} />
              </Route>
              <Route path="*" element={<ComingSoonPage />} />
            </Route>
          </Route>

          <Route path={ROUTES.FORBIDDEN} element={<ForbiddenPage />} />
          <Route path={ROUTES.TEST} element={<TestPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
