import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ApiError } from '../../core/services/api-client.service';
import { Booking, BookingRequest } from '../../core/models/types';
import { pageFromResponse } from '../../core/utils/schemas';
import { ErrorAlertComponent } from '../../components/ui/alert.component';
import { AsyncMultiSelectComponent } from '../../components/ui/multi-select.component';
import { PaginationComponent } from '../../components/ui/pagination.component';

const empty: BookingRequest = {
  price: 100, status: 'подтверждено', checkInDate: new Date().toISOString().slice(0, 10), duration: '1 ночь',
  guestId: undefined, roomIds: [], paymentIds: [],
};

@Component({
  selector: 'app-booking-admin',
  standalone: true,
  imports: [FormsModule, ErrorAlertComponent, AsyncMultiSelectComponent, PaginationComponent],
  template: `
    <div>
      <div class="admin-header-row">
        <h1 class="admin-title">Бронирования</h1>
        <button type="button" class="btn btn-primary" (click)="openCreate()">Добавить</button>
      </div>
      @if (error) { <app-error-alert [message]="error" /> }

      @if (showForm) {
        <div class="modal-overlay" (click)="closeForm()">
          <form class="modal card modal-lg" (click)="$event.stopPropagation()" (ngSubmit)="save()">
            <h2>{{ editId ? 'Редактировать' : 'Новое' }} бронирование</h2>
            <div class="form-group"><label>Гость (N:1)</label>
              <select [(ngModel)]="form.guestId" name="guestId">
                <option [ngValue]="undefined">— выберите —</option>
                @for (g of guestOptions; track g.id) {
                  <option [ngValue]="g.id">{{ g.label }}</option>
                }
              </select>
            </div>
            <div class="form-row">
              <div class="form-group"><label>Дата заезда</label><input type="date" required [(ngModel)]="form.checkInDate" name="checkInDate" /></div>
              <div class="form-group"><label>Длительность</label><input required [(ngModel)]="form.duration" name="duration" /></div>
            </div>
            <div class="form-row">
              <div class="form-group"><label>Цена</label><input type="number" min="1" required [(ngModel)]="form.price" name="price" /></div>
              <div class="form-group"><label>Статус</label>
                <select [(ngModel)]="form.status" name="status">
                  <option value="подтверждено">подтверждено</option>
                  <option value="заселен">заселен</option>
                  <option value="отменено">отменено</option>
                  <option value="на обработке">на обработке</option>
                </select>
              </div>
            </div>
            <app-async-multi-select label="Номера (M:N)" [selected]="form.roomIds ?? []" (selectedChange)="form.roomIds = $event" [loadOptions]="loadRoomOptions" />
            <app-async-multi-select label="Платежи (M:N)" [selected]="form.paymentIds ?? []" (selectedChange)="form.paymentIds = $event" [loadOptions]="loadPaymentOptions" />
            <div class="btn-row">
              <button type="submit" class="btn btn-primary">Сохранить</button>
              <button type="button" class="btn btn-outline" (click)="closeForm()">Отмена</button>
            </div>
          </form>
        </div>
      }

      @if (loading) { <div class="loading">Загрузка...</div> }
      <table class="data-table">
        <thead><tr><th>ID</th><th>Гость</th><th>Дата</th><th>Длительность</th><th>Номера</th><th>Платежи</th><th>Цена</th><th>Статус</th><th></th></tr></thead>
        <tbody>
          @for (b of items; track b.id) {
            <tr>
              <td>{{ b.id }}</td><td>{{ b.guest?.fullName ?? '—' }}</td><td>{{ b.checkInDate }}</td><td>{{ b.duration }}</td>
              <td>{{ formatRoomIds(b) }}</td><td>{{ formatPaymentIds(b) }}</td><td>{{ b.price }}</td>
              <td><span class="badge">{{ b.status }}</span></td>
              <td class="actions">
                <button type="button" class="btn btn-sm btn-outline" (click)="openEdit(b)">Изменить</button>
                <button type="button" class="btn btn-sm btn-danger" (click)="remove(b.id)">Удалить</button>
              </td>
            </tr>
          }
        </tbody>
      </table>
      <app-pagination [pageInfo]="pageInfo" (pageChange)="setPage($event)" />
    </div>
  `,
})
export class BookingAdminComponent implements OnInit {
  items: Booking[] = [];
  pageInfo = { page: 0, totalPages: 0, totalElements: 0 };
  form: BookingRequest = { ...empty };
  guestOptions: { id: number; label: string }[] = [];
  showForm = false;
  editId: number | null = null;
  error = '';
  loading = false;

  loadRoomOptions = async () => {
    const p = await this.api.room.list();
    return p.content.map((r) => ({ id: r.id, label: `${r.type} — этаж ${r.floor} (${r.price} BYN)` }));
  };

  loadPaymentOptions = async () => {
    const p = await this.api.payment.list();
    return p.content.map((pay) => ({ id: pay.id, label: `#${pay.id} — ${pay.amount} BYN (${pay.status})` }));
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.guest.list({ size: 100 }).then((p) => {
      this.guestOptions = p.content.map((g) => ({ id: g.id, label: `${g.fullName} (${g.email})` }));
    });
    this.load();
  }

  setPage(page: number): void { this.pageInfo.page = page; this.load(); }

  openCreate(): void { this.editId = null; this.form = { ...empty, checkInDate: new Date().toISOString().slice(0, 10) }; this.showForm = true; }

  openEdit(b: Booking): void {
    this.editId = b.id;
    this.form = {
      price: b.price, status: b.status, checkInDate: b.checkInDate, duration: b.duration,
      guestId: b.guest?.id, roomIds: b.rooms?.map((r) => r.id) ?? [], paymentIds: b.payments?.map((p) => p.id) ?? [],
    };
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; }

  formatRoomIds(b: Booking): string { return b.rooms?.map((r) => `#${r.id}`).join(', ') || '—'; }
  formatPaymentIds(b: Booking): string { return b.payments?.map((p) => `#${p.id}`).join(', ') || '—'; }

  async save(): Promise<void> {
    try {
      if (this.editId) await this.api.booking.update(this.editId, this.form);
      else await this.api.booking.create(this.form);
      this.closeForm();
      this.load();
    } catch (err) {
      this.error = err instanceof ApiError ? err.message : 'Ошибка';
    }
  }

  async remove(id: number): Promise<void> {
    if (!confirm('Удалить?')) return;
    await this.api.booking.delete(id);
    this.load();
  }

  private async load(): Promise<void> {
    this.loading = true;
    const data = await this.api.booking.list({ page: this.pageInfo.page, size: 10, sort: 'checkInDate,desc' });
    const parsed = pageFromResponse(data);
    this.items = parsed.content;
    this.pageInfo = { page: parsed.page, totalPages: parsed.totalPages, totalElements: parsed.totalElements };
    this.loading = false;
  }
}
