import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/services/api.service';
import { Booking } from '../../core/models/types';
import { ErrorAlertComponent } from '../../components/ui/alert.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ErrorAlertComponent],
  template: `
    <div>
      <h1 class="admin-title">Дашборд</h1>
      @if (error) { <app-error-alert [message]="error" /> }

      <div class="stats-grid">
        @for (s of statCards; track s.label) {
          <div class="stat-card">
            <div class="stat-icon">{{ s.label.charAt(0) }}</div>
            <div>
              <div class="stat-value">{{ s.value }}</div>
              <div class="stat-label">{{ s.label }}</div>
            </div>
          </div>
        }
      </div>

      <section class="card admin-section">
        <h2>Последние бронирования</h2>
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th><th>Гость</th><th>Дата</th><th>Номера</th><th>Цена</th><th>Статус</th>
            </tr>
          </thead>
          <tbody>
            @for (b of recentBookings; track b.id) {
              <tr>
                <td>{{ b.id }}</td>
                <td>{{ b.guest?.fullName ?? '—' }}</td>
                <td>{{ b.checkInDate }}</td>
                <td>{{ formatRooms(b) }}</td>
                <td>{{ b.price }} BYN</td>
                <td><span class="badge">{{ b.status }}</span></td>
              </tr>
            }
          </tbody>
        </table>
      </section>
    </div>
  `,
})
export class DashboardComponent implements OnInit {
  stats = { guests: 0, rooms: 0, bookings: 0, payments: 0, services: 0, employees: 0 };
  recentBookings: Booking[] = [];
  error = '';

  get statCards() {
    return [
      { label: 'Гости', value: this.stats.guests },
      { label: 'Номера', value: this.stats.rooms },
      { label: 'Бронирования', value: this.stats.bookings },
      { label: 'Платежи', value: this.stats.payments },
      { label: 'Услуги', value: this.stats.services },
      { label: 'Сотрудники', value: this.stats.employees },
    ];
  }

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    Promise.all([
      this.api.guest.list({ size: 1 }),
      this.api.room.list({ size: 1 }),
      this.api.booking.list({ size: 5, sort: 'checkInDate,desc' }),
      this.api.payment.list({ size: 1 }),
      this.api.service.list({ size: 1 }),
      this.api.employee.list({ size: 1 }),
    ])
      .then(([g, r, b, p, s, e]) => {
        this.stats = {
          guests: g.totalElements,
          rooms: r.totalElements,
          bookings: b.totalElements,
          payments: p.totalElements,
          services: s.totalElements,
          employees: e.totalElements,
        };
        this.recentBookings = b.content;
      })
      .catch((err) => {
        this.error = err instanceof Error ? err.message : 'Ошибка загрузки';
      });
  }

  formatRooms(b: Booking): string {
    return b.rooms?.map((r) => r.type).join(', ') || '—';
  }
}
