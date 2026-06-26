import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <header class="site-header">
      <div class="container header-inner">
        <a routerLink="/" class="logo">
          <span class="logo-mark">HM</span>
          <span class="logo-text">Hotel Manager</span>
        </a>
        <nav class="main-nav">
          <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Главная</a>
          <a routerLink="/rooms" routerLinkActive="active">Номера</a>
          <a routerLink="/booking" routerLinkActive="active">Забронировать</a>
          <a routerLink="/services" routerLinkActive="active">Услуги</a>
          @if (auth.isAdmin) {
            <a routerLink="/admin" routerLinkActive="active">Панель</a>
          }
        </nav>
        <div class="header-actions">
          @if (auth.guest) {
            <a routerLink="/profile" class="profile-link">{{ auth.guest.fullName }}</a>
            <button type="button" class="btn-logout" (click)="auth.logout()">Выйти</button>
          } @else {
            <a routerLink="/login" class="btn-login">Войти</a>
            <a routerLink="/register" class="btn-register">Регистрация</a>
          }
        </div>
      </div>
    </header>
  `,
})
export class HeaderComponent {
  constructor(public auth: AuthService) {}
}

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  template: `
    <footer class="site-footer">
      <div class="container footer-grid">
        <div>
          <h4>Hotel Manager</h4>
          <p>Комфортабельный отель в центре города. Бронирование онлайн 24/7.</p>
        </div>
        <div>
          <h4>Контакты</h4>
          <p>+375 (17) 123-45-67</p>
          <p>info&#64;hotelmanager.by</p>
        </div>
        <div>
          <h4>Навигация</h4>
          <a routerLink="/rooms">Номера</a>
          <a routerLink="/services">Услуги</a>
          <a routerLink="/admin/login">Администрирование</a>
        </div>
      </div>
      <div class="footer-bottom">© 2026 Hotel Manager Application</div>
    </footer>
  `,
})
export class FooterComponent {}
