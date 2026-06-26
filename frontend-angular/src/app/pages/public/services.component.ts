import { Component, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { ApiError } from '../../core/services/api-client.service';
import { Service } from '../../core/models/types';
import { ErrorAlertComponent, SuccessAlertComponent } from '../../components/ui/alert.component';

@Component({
  selector: 'app-services',
  standalone: true,
  imports: [DecimalPipe, ErrorAlertComponent, SuccessAlertComponent],
  template: `
    <div class="container page">
      <div class="page-header">
        <h1>Услуги отеля</h1>
        <p>SPA, питание, трансфер и дополнительные сервисы</p>
      </div>

      @if (error) {
        <app-error-alert [message]="error" (onClose)="error = ''" />
      }
      @if (success) {
        <app-success-alert [message]="success" />
      }
      @if (loading) {
        <div class="loading">Загрузка...</div>
      }

      <div class="services-grid">
        @for (s of services; track s.id) {
          <article class="service-card card">
            <div class="service-badge">{{ s.duration }}</div>
            <h3>{{ s.title }}</h3>
            <p>{{ s.description }}</p>
            <div class="service-meta">
              <span>{{ s.duration }}</span>
              <strong>{{ s.price }} BYN</strong>
            </div>
            <button type="button" class="btn btn-primary btn-block" (click)="orderService(s)" [disabled]="orderingId === s.id">
              @if (!auth.guest) {
                Войти для заказа
              } @else if (orderingId === s.id) {
                Добавление...
              } @else if (auth.guest.serviceIds?.includes(s.id)) {
                Добавлено
              } @else {
                Заказать
              }
            </button>
          </article>
        }
      </div>

      @if (!loading && !services.length) {
        <div class="empty-state">Услуги не найдены</div>
      }
    </div>
  `,
})
export class ServicesComponent implements OnInit {
  services: Service[] = [];
  loading = false;
  error = '';
  success = '';
  orderingId: number | null = null;

  constructor(
    public auth: AuthService,
    private api: ApiService,
  ) {}

  ngOnInit(): void {
    this.load();
  }

  async orderService(service: Service): Promise<void> {
    if (!this.auth.guest) {
      this.error = 'Войдите, чтобы заказать услугу';
      return;
    }
    this.error = '';
    this.success = '';
    this.orderingId = service.id;
    try {
      const currentIds = this.auth.guest.serviceIds ?? [];
      if (currentIds.includes(service.id)) {
        this.success = `Услуга «${service.title}» уже добавлена к вашему профилю`;
        return;
      }
      const updated = await this.api.guest.update(this.auth.guest.id, {
        fullName: this.auth.guest.fullName,
        phoneNumber: this.auth.guest.phoneNumber,
        email: this.auth.guest.email,
        status: this.auth.guest.status,
        serviceIds: [...currentIds, service.id],
      });
      this.auth.setGuest(updated);
      this.success = `Услуга «${service.title}» добавлена к вашему профилю`;
    } catch (e) {
      this.error = e instanceof ApiError ? e.message : 'Ошибка';
    } finally {
      this.orderingId = null;
    }
  }

  private async load(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      const data = await this.api.service.list({ size: 100 });
      this.services = data.content;
    } catch (e) {
      this.error = e instanceof Error ? e.message : 'Ошибка загрузки услуг';
      this.services = [];
    } finally {
      this.loading = false;
    }
  }
}
