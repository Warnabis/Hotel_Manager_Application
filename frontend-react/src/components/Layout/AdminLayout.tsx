import { NavLink, Outlet, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Layout.css';

const adminLinks = [
  { to: '/admin', label: 'Дашборд', end: true },
  { to: '/admin/guests', label: 'Гости' },
  { to: '/admin/rooms', label: 'Номера' },
  { to: '/admin/bookings', label: 'Бронирования' },
  { to: '/admin/payments', label: 'Платежи' },
  { to: '/admin/services', label: 'Услуги' },
  { to: '/admin/employees', label: 'Сотрудники' },
  { to: '/admin/positions', label: 'Должности' },
];

export function AdminLayout() {
  const { isAdmin, logout } = useAuth();

  if (!isAdmin) return <Navigate to="/admin/login" replace />;

  return (
    <div className="admin-shell">
      <aside className="admin-sidebar">
        <div className="admin-brand">Админ-панель</div>
        <nav className="admin-nav">
          {adminLinks.map((l) => (
            <NavLink key={l.to} to={l.to} end={l.end}>{l.label}</NavLink>
          ))}
        </nav>
        <div className="admin-sidebar-footer">
          <NavLink to="/">← На сайт</NavLink>
          <button type="button" className="btn btn-ghost btn-sm" onClick={logout}>Выйти</button>
        </div>
      </aside>
      <main className="admin-content">
        <Outlet />
      </main>
    </div>
  );
}
