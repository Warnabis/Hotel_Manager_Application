import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Layout.css';

export function Header() {
  const { guest, isAdmin, logout } = useAuth();

  return (
      <header className="site-header">
        <div className="container header-inner">
          <Link to="/" className="logo">
            <span className="logo-mark">HM</span>
            <span className="logo-text">Hotel Manager</span>
          </Link>
          <nav className="main-nav">
            <NavLink to="/" end>Главная</NavLink>
            <NavLink to="/rooms">Номера</NavLink>
            <NavLink to="/booking">Забронировать</NavLink>
            <NavLink to="/services">Услуги</NavLink>
            {isAdmin && <NavLink to="/admin">Панель</NavLink>}
          </nav>
          <div className="header-actions">
            {guest ? (
                <>
                  <Link to="/profile" className="profile-link">
                     {guest.fullName}
                  </Link>
                  <button type="button" className="btn-logout" onClick={logout}>
                    Выйти
                  </button>
                </>
            ) : (
                <>
                  <Link to="/login" className="btn-login">Войти</Link>
                  <Link to="/register" className="btn-register">Регистрация</Link>
                </>
            )}
          </div>
        </div>
      </header>
  );
}

export function Footer() {
  return (
      <footer className="site-footer">
        <div className="container footer-grid">
          <div>
            <h4>Hotel Manager</h4>
            <p>Комфортабельный отель в центре города. Бронирование онлайн 24/7.</p>
          </div>
          <div>
            <h4>Контакты</h4>
            <p>+375 (17) 123-45-67</p>
            <p>info@hotelmanager.by</p>
          </div>
          <div>
            <h4>Навигация</h4>
            <Link to="/rooms">Номера</Link>
            <Link to="/services">Услуги</Link>
            <Link to="/admin/login">Администрирование</Link>
          </div>
        </div>
        <div className="footer-bottom">© 2026 Hotel Manager Application</div>
      </footer>
  );
}