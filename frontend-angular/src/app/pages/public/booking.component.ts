import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { ApiError } from '../../core/services/api-client.service';
import { Room } from '../../core/models/types';
import { durationToNights, ROOM_TYPE_LABELS } from '../../core/utils/schemas';
import { ErrorAlertComponent, SuccessAlertComponent } from '../../components/ui/alert.component';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [FormsModule, RouterLink, DecimalPipe, ErrorAlertComponent, SuccessAlertComponent],
  template: `
    @if (!auth.guest) {
      <div class="container page">
        <div class="auth-prompt">
          <h2>Для бронирования необходимо войти</h2>
          <p>Войдите в аккаунт или зарегистрируйтесь, чтобы оформить бронь</p>
          <div class="btn-row">
            <a routerLink="/login" [queryParams]="{ redirect: '/booking' }" class="btn btn-primary">Войти</a>
            <a routerLink="/register" class="btn btn-outline">Регистрация</a>
          </div>
        </div>
      </div>
    } @else {
      <div class="container page">
        <div class="page-header">
          <h1>Оформление бронирования</h1>
          <p>Гость: <strong>{{ auth.guest.fullName }}</strong> ({{ auth.guest.email }})</p>
        </div>

        @if (error) {
          <app-error-alert [message]="error" [validationErrors]="validationErrors" (onClose)="error = ''" />
        }
        @if (success) {
          <app-success-alert [message]="success" />
        }

        <form class="booking-form card" (ngSubmit)="handleSubmit()">
          <div class="form-row">
            <div class="form-group">
              <label>Дата заезда *</label>
              <input type="date" required [(ngModel)]="checkInDate" name="checkInDate" />
            </div>
            <div class="form-group">
              <label>Длительность *</label>
              <select [(ngModel)]="duration" name="duration">
                <option>1 ночь</option>
                <option>2 ночи</option>
                <option>3 ночи</option>
                <option>1 неделя</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <div class="form-label-row">
              <label>Номера *</label>
              <button type="button" class="btn btn-sm btn-outline" (click)="goToRooms()">Выбрать номер</button>
            </div>
            <div class="room-checkboxes">
              @for (room of availableRooms; track room.id) {
                <label class="room-checkbox">
                  <input type="checkbox" [checked]="selectedRooms.includes(room.id)" (change)="toggleRoom(room.id)" />
                  <span>{{ roomTypeLabel(room) }} — этаж {{ room.floor }} — {{ room.price }} BYN/ночь</span>
                </label>
              }
            </div>
          </div>

          <div class="selected-rooms" style="margin-bottom: 0.5rem">
            <div class="form-label-row">
              <strong>Выбрано:</strong>
              <div>
                @if (selectedRooms.length) {
                  <button type="button" class="btn btn-sm btn-outline" (click)="clearSelection()">Очистить всё</button>
                }
              </div>
            </div>
            @if (selectedRoomObjects.length) {
              <ul style="list-style: none; padding: 0; margin: 0.5rem 0">
                @for (r of selectedRoomObjects; track r.id) {
                  <li style="display: flex; justify-content: space-between; align-items: center; padding: 0.25rem 0">
                    <span>{{ roomTypeLabel(r) }} — {{ r.price }} BYN</span>
                    <button type="button" class="btn btn-sm btn-danger" (click)="toggleRoom(r.id)">Убрать</button>
                  </li>
                }
              </ul>
            } @else {
              <span class="text-muted">нет</span>
            }
          </div>

          <div class="booking-summary">
            <strong>Итого: {{ totalPrice }} BYN</strong>
            @if (nights > 1) {
              <span class="text-muted"> ({{ nights }} ночей)</span>
            }
          </div>

          <div class="form-group">
            <label>Способ оплаты</label>
            <select [(ngModel)]="paymentMethod" name="paymentMethod">
              <option value="card">Банковская карта</option>
              <option value="cash">Наличные при заезде</option>
              <option value="transfer">Банковский перевод</option>
            </select>
          </div>

          <button type="submit" class="btn btn-accent btn-lg btn-block" [disabled]="submitting">
            {{ submitting ? 'Оформление...' : 'Подтвердить бронирование' }}
          </button>
        </form>
      </div>
    }
  `,
})
export class BookingComponent implements OnInit {
  allRooms: Room[] = [];
  selectedRooms: number[] = [];
  checkInDate = '';
  duration = '1 ночь';
  paymentMethod = 'card';
  error = '';
  validationErrors?: Record<string, string>;
  success = '';
  submitting = false;

  constructor(
    public auth: AuthService,
    private api: ApiService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params: Record<string, any>) => {
      const preRoomId = params['roomId'];
      this.checkInDate = params['checkIn'] ?? '';
      this.duration = params['duration'] ?? '1 ночь';

      if (params['selectedRooms']) {
        const ids = (params['selectedRooms'] as string).split(',').map(Number);
        ids.forEach(id => {
          if (!this.selectedRooms.includes(id)) {
            this.selectedRooms = [...this.selectedRooms, id];
          }
        });
      }

      if (preRoomId) {
        const id = Number(preRoomId);
        if (!this.selectedRooms.includes(id)) {
          this.selectedRooms = [...this.selectedRooms, id];
        }
      }
    });
    this.loadRooms();
  }

  get availableRooms(): Room[] {
    return this.allRooms.filter((r) => r.status === 'available');
  }

  get selectedRoomObjects(): Room[] {
    return this.allRooms.filter((r) => this.selectedRooms.includes(r.id));
  }

  get nights(): number {
    return durationToNights(this.duration);
  }

  get totalPrice(): number {
    const nightly = this.selectedRoomObjects.reduce((sum, r) => sum + r.price, 0);
    return nightly * this.nights;
  }

  roomTypeLabel(room: Room): string {
    return ROOM_TYPE_LABELS[room.type.toLowerCase()] ?? room.type;
  }

  toggleRoom(id: number): void {
    this.selectedRooms = this.selectedRooms.includes(id)
      ? this.selectedRooms.filter((x) => x !== id)
      : [...this.selectedRooms, id];
  }

  clearSelection(): void {
    this.selectedRooms = [];
  }

  goToRooms(): void {
    const queryParams: Record<string, any> = {
      checkIn: this.checkInDate,
      duration: this.duration,
      returnUrl: '/booking'
    };

    if (this.selectedRooms.length) {
      queryParams['selectedRooms'] = this.selectedRooms.join(',');
    }

    this.router.navigate(['/rooms'], { queryParams });
  }

  async handleSubmit(): Promise<void> {
    if (!this.auth.guest) {
      this.router.navigate(['/login'], { queryParams: { redirect: '/booking' } });
      return;
    }
    if (!this.selectedRooms.length) {
      this.error = 'Выберите хотя бы один номер';
      return;
    }
    if (!this.checkInDate) {
      this.error = 'Укажите дату заезда';
      return;
    }
    this.submitting = true;
    this.error = '';
    this.validationErrors = undefined;
    try {
      const booking = await this.api.booking.create({
        price: this.totalPrice,
        status: 'confirmed',
        checkInDate: this.checkInDate,
        duration: this.duration,
        guestId: this.auth.guest.id,
        roomIds: this.selectedRooms,
        paymentIds: [],
      });
      if (this.totalPrice > 0) {
        try {
          await this.api.payment.create({
            status: 'completed',
            amount: this.totalPrice,
            paymentDate: this.checkInDate,
            paymentMethod: this.paymentMethod,
            bookingIds: [booking.id],
            serviceIds: [],
          });
        } catch {

        }
      }
      this.success = 'Бронирование успешно создано!';
      setTimeout(() => this.router.navigate(['/profile']), 2000);
    } catch (err) {
      let message = 'Ошибка при бронировании';
      if (err instanceof ApiError) {
        message = err.message;
        this.validationErrors = err.validationErrors;
        if (message && (message.toLowerCase().includes('room') || message.toLowerCase().includes('constraint'))) {
          message = 'Сервер не позволяет забронировать несколько комнат в одном заказе. Оформите отдельные брони.';
        }
      }
      this.error = message;
    } finally {
      this.submitting = false;
    }
  }

  private async loadRooms(): Promise<void> {
    try {
      const data = await this.api.room.list({ size: 100 });
      this.allRooms = data.content;
    } catch {

    }
  }
}
