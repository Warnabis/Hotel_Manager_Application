import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { HeaderComponent, FooterComponent } from './header.component';

@Component({
  selector: 'app-public-layout',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  template: `
    <app-header />
    <main style="flex: 1">
      <router-outlet />
    </main>
    <app-footer />
  `,
})
export class PublicLayoutComponent {}

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="admin-shell">
      <aside class="admin-sidebar">
        <div class="admin-brand">Админ-панель</div>
        <nav class="admin-nav">
          @for (link of adminLinks; track link.to) {
            <a [routerLink]="link.to" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: link.end ?? false }">{{ link.label }}</a>
          }
        </nav>
        <div class="admin-sidebar-footer">
          <a routerLink="/">← На сайт</a>
          <button type="button" class="btn btn-ghost btn-sm" (click)="logout()">Выйти</button>
        </div>
      </aside>
      <main class="admin-content">
        <router-outlet />
      </main>
    </div>
  `,
})
export class AdminLayoutComponent {
  adminLinks = [
    { to: '/admin', label: 'Дашборд', end: true },
    { to: '/admin/guests', label: 'Гости' },
    { to: '/admin/rooms', label: 'Номера' },
    { to: '/admin/bookings', label: 'Бронирования' },
    { to: '/admin/payments', label: 'Платежи' },
    { to: '/admin/services', label: 'Услуги' },
    { to: '/admin/employees', label: 'Сотрудники' },
    { to: '/admin/positions', label: 'Должности' },
  ];

  constructor(private auth: AuthService) {}

  logout(): void {
    this.auth.logout();
  }
}
