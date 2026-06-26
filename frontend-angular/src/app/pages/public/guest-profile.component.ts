import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { ApiError } from '../../core/services/api-client.service';
import { Booking, Service } from '../../core/models/types';
import { ErrorAlertComponent, SuccessAlertComponent } from '../../components/ui/alert.component';

@Component({
  selector: 'app-guest-profile',
  standalone: true,
  imports: [FormsModule, RouterLink, DecimalPipe, ErrorAlertComponent, SuccessAlertComponent],
  template: `
    <div class="container page">
      <div class="page-header">
        <h1>Мой профиль</h1>
        <p>Управление бронированиями и услугами</p>
      </div>

      @if (error) { <app-error-alert [message]="error" (onClose)="error = ''" /> }
      @if (success) { <app-success-alert [message]="success" /> }

      <div class="profile-grid">
        <section class="card profile-card">
          <div class="section-header-row">
            <h2>Личные данные</h2>
            @if (!isEditing && !isChangingPassword) {
              <button type="button" class="btn btn-sm btn-outline" (click)="startEditing()">Редактировать</button>
            }
          </div>

          @if (isEditing) {
            <form (ngSubmit)="handleEditSubmit()">
              <div class="form-group">
                <label>ФИО *</label>
                <input type="text" [(ngModel)]="editForm.fullName" name="fullName" required />
              </div>
              <div class="form-group">
                <label>Телефон *</label>
                <input type="text" [(ngModel)]="editForm.phoneNumber" name="phoneNumber" required />
              </div>
              <div class="form-group">
                <label>Email *</label>
                <input type="email" [(ngModel)]="editForm.email" name="email" required />
              </div>
              <div class="btn-row" style="margin-top: 1rem">
                <button type="submit" class="btn btn-primary" [disabled]="isSubmitting">{{ isSubmitting ? 'Сохранение...' : 'Сохранить' }}</button>
                <button type="button" class="btn btn-outline" (click)="cancelEditing()">Отмена</button>
              </div>
            </form>
          } @else if (isChangingPassword) {
            <form (ngSubmit)="handlePasswordSubmit()">
              <div class="form-group">
                <label>Новый пароль *</label>
                <input type="password" [(ngModel)]="passwordForm.newPassword" name="newPassword" required minlength="6" />
              </div>
              <div class="form-group">
                <label>Подтверждение пароля *</label>
                <input type="password" [(ngModel)]="passwordForm.confirmPassword" name="confirmPassword" required />
              </div>
              <div class="btn-row" style="margin-top: 1rem">
                <button type="submit" class="btn btn-primary" [disabled]="isPasswordSubmitting">{{ isPasswordSubmitting ? 'Сохранение...' : 'Сменить пароль' }}</button>
                <button type="button" class="btn btn-outline" (click)="cancelPasswordChange()">Отмена</button>
              </div>
            </form>
          } @else {
            <dl class="detail-list">
              <dt>ФИО</dt><dd>{{ auth.guest!.fullName }}</dd>
              <dt>Email</dt><dd>{{ auth.guest!.email }}</dd>
              <dt>Телефон</dt><dd>{{ auth.guest!.phoneNumber }}</dd>
              <dt>Статус</dt><dd><span class="badge">{{ auth.guest!.status }}</span></dd>
            </dl>
            <div class="btn-row">
              <a routerLink="/booking" class="btn btn-accent">Новое бронирование</a>
              <a routerLink="/services" class="btn btn-outline">Каталог услуг</a>
            </div>
            <div style="margin-top: 1rem">
              <button type="button" class="btn btn-outline btn-sm" (click)="startPasswordChange()">Сменить пароль</button>
            </div>
          }

          <div class="profile-danger-zone" style="margin-top: 1.5rem">
            <button type="button" class="btn btn-danger btn-block" (click)="handleDeleteAccount()" [disabled]="deleting">
              {{ deleting ? 'Удаление...' : 'Удалить аккаунт' }}
            </button>
          </div>
        </section>

        <section class="card">
          <div class="section-header-row"><h2>Мои бронирования</h2></div>
          @if (bookingsLoading) { <div class="loading">Загрузка...</div> }
          @if (!bookingsLoading && !bookings.length) {
            <p class="text-muted">У вас пока нет бронирований</p>
          }
          <div class="booking-list">
            @for (b of bookings; track b.id) {
              <div class="booking-item">
                <div style="display: flex; justify-content: space-between; align-items: center">
                  <div>
                    <strong>Бронь #{{ b.id }}</strong>
                    <span class="badge" [class]="'badge-' + b.status">{{ b.status }}</span>
                  </div>
                  <button type="button" class="btn btn-sm btn-danger" (click)="cancelBooking(b)">Отменить</button>
                </div>
                <p>Заезд: {{ b.checkInDate }} · {{ b.duration }}</p>
                <p>Номера: {{ formatRooms(b) }}</p>
                <p>Цена: {{ b.price }} BYN</p>
                @if (b.payments?.length) {
                  <p>Платежи: {{ formatPayments(b) }}</p>
                }
              </div>
            }
          </div>
        </section>

        <section class="card">
          <div class="section-header-row">
            <h2>Мои услуги</h2>
            <button type="button" class="btn btn-sm btn-outline" (click)="handleRefresh()">Обновить</button>
          </div>
          @if (servicesLoading) { <div class="loading">Загрузка...</div> }
          @if (!servicesLoading && !services.length) {
            <p class="text-muted">Нет заказанных услуг. <a routerLink="/services">Посмотреть каталог</a></p>
          } @else {
            <ul class="service-list profile-service-list">
              @for (s of services; track s.id) {
                <li>
                  <strong>{{ s.title }}</strong>
                  <span>{{ s.price }} BYN · {{ s.duration }}</span>
                  <button type="button" class="btn btn-sm btn-danger" (click)="removeService(s.id)">Отменить</button>
                </li>
              }
            </ul>
          }
        </section>
      </div>
    </div>
  `,
})
export class GuestProfileComponent implements OnInit {
  bookings: Booking[] = [];
  services: Service[] = [];
  bookingsLoading = false;
  servicesLoading = false;
  error = '';
  success = '';
  deleting = false;
  isEditing = false;
  isChangingPassword = false;
  isSubmitting = false;
  isPasswordSubmitting = false;
  editForm = { fullName: '', phoneNumber: '', email: '' };
  passwordForm = { newPassword: '', confirmPassword: '' };

  constructor(
    public auth: AuthService,
    private api: ApiService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    if (!this.auth.guest) {
      this.router.navigate(['/login'], { queryParams: { redirect: '/profile' } });
      return;
    }
    this.loadBookings();
    this.loadServices();
  }

  startEditing(): void {
    if (!this.auth.guest) return;
    this.editForm = {
      fullName: this.auth.guest.fullName,
      phoneNumber: this.auth.guest.phoneNumber,
      email: this.auth.guest.email,
    };
    this.isEditing = true;
  }

  cancelEditing(): void {
    this.isEditing = false;
    this.error = '';
  }

  startPasswordChange(): void {
    this.passwordForm = { newPassword: '', confirmPassword: '' };
    this.isChangingPassword = true;
    this.error = '';
  }

  cancelPasswordChange(): void {
    this.isChangingPassword = false;
    this.error = '';
  }

  async handleEditSubmit(): Promise<void> {
    if (!this.auth.guest) return;
    this.isSubmitting = true;
    this.error = '';
    try {
      const updated = await this.api.guest.update(this.auth.guest.id, {
        fullName: this.editForm.fullName.trim(),
        phoneNumber: this.editForm.phoneNumber.trim(),
        email: this.editForm.email.trim().toLowerCase(),
        status: this.auth.guest.status,
        serviceIds: this.auth.guest.serviceIds || [],
      });
      this.auth.setGuest(updated);
      this.isEditing = false;
      this.showSuccess('Данные успешно обновлены');
    } catch (e) {
      this.error = e instanceof ApiError ? e.message : 'Ошибка обновления данных';
    } finally {
      this.isSubmitting = false;
    }
  }

  async handlePasswordSubmit(): Promise<void> {
    if (!this.auth.guest) return;
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.error = 'Пароли не совпадают';
      return;
    }
    if (this.passwordForm.newPassword.length < 6) {
      this.error = 'Пароль должен содержать минимум 6 символов';
      return;
    }
    this.isPasswordSubmitting = true;
    this.error = '';
    try {
      await this.api.auth.changePassword({ newPassword: this.passwordForm.newPassword });
      this.isChangingPassword = false;
      this.passwordForm = { newPassword: '', confirmPassword: '' };
      this.showSuccess('Пароль успешно изменён');
    } catch (e) {
      if (e instanceof ApiError && (e.status === 200 || e.status === 204)) {
        this.isChangingPassword = false;
        this.showSuccess('Пароль успешно изменён');
      } else {
        this.error = e instanceof ApiError ? e.message : 'Ошибка изменения пароля';
      }
    } finally {
      this.isPasswordSubmitting = false;
    }
  }

  async handleDeleteAccount(): Promise<void> {
    if (!confirm('Удалить аккаунт без возможности восстановления?')) return;
    this.deleting = true;
    this.error = '';
    try {
      await this.api.auth.deleteAccount();
      this.auth.logout();
      this.router.navigate(['/']);
    } catch (e) {
      this.error = e instanceof ApiError ? e.message : 'Ошибка удаления';
    } finally {
      this.deleting = false;
    }
  }

  handleRefresh(): void {
    this.auth.refreshGuest().catch(() => {});
    this.loadServices();
  }

  async removeService(serviceId: number): Promise<void> {
    if (!this.auth.guest) return;
    const newIds = (this.auth.guest.serviceIds ?? []).filter((id) => id !== serviceId);
    try {
      const updated = await this.api.guest.update(this.auth.guest.id, {
        fullName: this.auth.guest.fullName,
        phoneNumber: this.auth.guest.phoneNumber,
        email: this.auth.guest.email,
        status: this.auth.guest.status,
        serviceIds: newIds,
      });
      this.auth.setGuest(updated);
      await this.loadServices();
      this.showSuccess('Услуга удалена');
    } catch (e) {
      this.error = e instanceof ApiError ? e.message : 'Ошибка удаления услуги';
    }
  }

  async cancelBooking(booking: Booking): Promise<void> {
    if (!confirm('Отменить бронирование?')) return;
    try {
      await this.api.booking.delete(booking.id);
      await this.loadBookings();
      this.showSuccess(`Бронирование #${booking.id} отменено`);
    } catch (e) {
      this.error = e instanceof ApiError ? e.message : 'Ошибка отмены бронирования';
    }
  }

  private showSuccess(msg: string): void {
    this.success = msg;
    setTimeout(() => (this.success = ''), 3000);
  }

  formatRooms(b: Booking): string {
    return b.rooms?.map((r) => r.type).join(', ') || '—';
  }

  formatPayments(b: Booking): string {
    return b.payments?.map((p) => `#${p.id} (${p.amount} BYN)`).join(', ') ?? '';
  }

  private async loadBookings(): Promise<void> {
    if (!this.auth.guest) return;
    this.bookingsLoading = true;
    try {
      const data = await this.api.booking.list({ guestId: this.auth.guest.id });
      this.bookings = data.content;
    } catch {
      this.bookings = [];
    } finally {
      this.bookingsLoading = false;
    }
  }

  private async loadServices(): Promise<void> {
    if (!this.auth.guest) return;
    this.servicesLoading = true;
    try {
      const g = await this.api.guest.get(this.auth.guest.id);
      if (!g.serviceIds?.length) {
        this.services = [];
      } else {
        const all = await this.api.service.list({ size: 100 });
        this.services = all.content.filter((s) => g.serviceIds!.includes(s.id));
      }
    } catch {
      this.services = [];
    } finally {
      this.servicesLoading = false;
    }
  }
}
