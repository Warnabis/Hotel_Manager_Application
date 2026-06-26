import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ApiError } from '../../core/services/api-client.service';
import { Payment, PaymentRequest } from '../../core/models/types';
import { ErrorAlertComponent } from '../../components/ui/alert.component';
import { AsyncMultiSelectComponent } from '../../components/ui/multi-select.component';

const empty: PaymentRequest = {
  status: 'не оплачено', amount: 0, paymentDate: new Date().toISOString().slice(0, 10),
  paymentMethod: 'наличные', bookingIds: [], serviceIds: [],
};

@Component({
  selector: 'app-payment-admin',
  standalone: true,
  imports: [FormsModule, ErrorAlertComponent, AsyncMultiSelectComponent],
  template: `
    <div>
      <div class="admin-header-row">
        <h1 class="admin-title">Платежи</h1>
        <button type="button" class="btn btn-primary" (click)="openCreate()">Добавить</button>
      </div>
      @if (error) { <app-error-alert [message]="error" /> }

      @if (showForm) {
        <div class="modal-overlay" (click)="closeForm()">
          <form class="modal card" (click)="$event.stopPropagation()" (ngSubmit)="save()">
            <h2>{{ editId ? 'Редактировать' : 'Новый' }} платёж</h2>
            <div class="form-row">
              <div class="form-group"><label>Сумма</label><input type="number" min="0" required [(ngModel)]="form.amount" name="amount" /></div>
              <div class="form-group"><label>Дата</label><input type="date" required [(ngModel)]="form.paymentDate" name="paymentDate" /></div>
            </div>
            <div class="form-row">
              <div class="form-group"><label>Способ</label>
                <select [(ngModel)]="form.paymentMethod" name="paymentMethod">
                  <option value="наличные">наличные</option>
                  <option value="карта">карта</option>
                  <option value="банковский перевод">банковский перевод</option>
                </select>
              </div>
              <div class="form-group"><label>Статус</label>
                <select [(ngModel)]="form.status" name="status">
                  <option value="оплачено">оплачено</option>
                  <option value="не оплачено">не оплачено</option>
                </select>
              </div>
            </div>
            <app-async-multi-select label="Бронирования (M:N)" [selected]="form.bookingIds ?? []" (selectedChange)="form.bookingIds = $event" [loadOptions]="loadBookingOptions" />
            <app-async-multi-select label="Услуги (M:N)" [selected]="form.serviceIds ?? []" (selectedChange)="form.serviceIds = $event" [loadOptions]="loadServiceOptions" />
            <div class="btn-row">
              <button type="submit" class="btn btn-primary">Сохранить</button>
              <button type="button" class="btn btn-outline" (click)="closeForm()">Отмена</button>
            </div>
          </form>
        </div>
      }

      <table class="data-table">
        <thead><tr><th>ID</th><th>Сумма</th><th>Дата</th><th>Способ</th><th>Статус</th><th>Брони</th><th>Услуги</th><th></th></tr></thead>
        <tbody>
          @for (p of items; track p.id) {
            <tr>
              <td>{{ p.id }}</td><td>{{ p.amount }} BYN</td><td>{{ p.paymentDate }}</td><td>{{ p.paymentMethod }}</td>
              <td><span class="badge">{{ p.status }}</span></td>
              <td>{{ p.bookingIds?.join(', ') || '—' }}</td>
              <td>{{ p.serviceIds?.join(', ') || '—' }}</td>
              <td class="actions">
                <button type="button" class="btn btn-sm btn-outline" (click)="openEdit(p)">Изменить</button>
                <button type="button" class="btn btn-sm btn-danger" (click)="remove(p.id)">Удалить</button>
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
})
export class PaymentAdminComponent implements OnInit {
  items: Payment[] = [];
  form: PaymentRequest = { ...empty };
  showForm = false;
  editId: number | null = null;
  error = '';

  loadBookingOptions = async () => {
    const p = await this.api.booking.list();
    return p.content.map((b) => ({ id: b.id, label: `#${b.id} — ${b.guest?.fullName ?? '?'} (${b.checkInDate})` }));
  };

  loadServiceOptions = async () => {
    const p = await this.api.service.list();
    return p.content.map((s) => ({ id: s.id, label: s.title }));
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  openCreate(): void { this.editId = null; this.form = { ...empty, paymentDate: new Date().toISOString().slice(0, 10) }; this.showForm = true; }

  openEdit(p: Payment): void {
    this.editId = p.id;
    this.form = { status: p.status, amount: p.amount, paymentDate: p.paymentDate, paymentMethod: p.paymentMethod, bookingIds: p.bookingIds ?? [], serviceIds: p.serviceIds ?? [] };
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; }

  async save(): Promise<void> {
    try {
      if (this.editId) await this.api.payment.update(this.editId, this.form);
      else await this.api.payment.create(this.form);
      this.closeForm();
      this.load();
    } catch (err) {
      this.error = err instanceof ApiError ? err.message : 'Ошибка';
    }
  }

  async remove(id: number): Promise<void> {
    if (!confirm('Удалить?')) return;
    await this.api.payment.delete(id);
    this.load();
  }

  private async load(): Promise<void> {
    const data = await this.api.payment.list({ sort: 'paymentDate,desc' });
    this.items = data.content;
  }
}
