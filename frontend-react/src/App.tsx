import { BrowserRouter, Routes, Route, Outlet } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Header, Footer } from './components/Layout/Header';
import { AdminLayout } from './components/Layout/AdminLayout';
import { HomePage } from './pages/public/HomePage';
import { RoomsPage } from './pages/public/RoomsPage';
import { BookingPage } from './pages/public/BookingPage';
import { ServicesPage } from './pages/public/ServicesPage';
import { LoginPage, RegisterPage, AdminLoginPage } from './pages/public/AuthPages';
import { GuestProfilePage } from './pages/public/GuestProfilePage';
import { DashboardPage } from './pages/admin/DashboardPage';
import { GuestAdminPage } from './pages/admin/GuestAdminPage';
import { RoomAdminPage } from './pages/admin/RoomAdminPage';
import { BookingAdminPage } from './pages/admin/BookingAdminPage';
import { PaymentAdminPage } from './pages/admin/PaymentAdminPage';
import { ServiceAdminPage } from './pages/admin/ServiceAdminPage';
import { EmployeeAdminPage } from './pages/admin/EmployeeAdminPage';
import { PositionAdminPage } from './pages/admin/PositionAdminPage';

function PublicLayout() {
  return (
      <>
        <Header />
        <main style={{ flex: 1 }}>
          <Outlet />
        </main>
        <Footer />
      </>
  );
}

export default function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<PublicLayout />}>
              <Route index element={<HomePage />} />
              <Route path="rooms" element={<RoomsPage />} />
              <Route path="booking" element={<BookingPage />} />
              <Route path="services" element={<ServicesPage />} />
              <Route path="login" element={<LoginPage />} />
              <Route path="register" element={<RegisterPage />} />
              <Route path="profile" element={<GuestProfilePage />} />
            </Route>
            <Route path="admin/login" element={<AdminLoginPage />} />
            <Route path="admin" element={<AdminLayout />}>
              <Route index element={<DashboardPage />} />
              <Route path="guests" element={<GuestAdminPage />} />
              <Route path="rooms" element={<RoomAdminPage />} />
              <Route path="bookings" element={<BookingAdminPage />} />
              <Route path="payments" element={<PaymentAdminPage />} />
              <Route path="services" element={<ServiceAdminPage />} />
              <Route path="employees" element={<EmployeeAdminPage />} />
              <Route path="positions" element={<PositionAdminPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
  );
}