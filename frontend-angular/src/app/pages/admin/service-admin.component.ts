import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ApiError } from '../../core/services/api-client.service';
import { Service, ServiceRequest } from '../../core/models/types';
import { ErrorAlertComponent } from '../../components/ui/alert.component';
import { AsyncMultiSelectComponent } from '../../components/ui/multi-select.component';

const empty: ServiceRequest = { title: '', description: '', price: 0, duration: '1 час', guestIds: [], employeeIds: [] };

@Component({
  selector: 'app-service-admin',
  standalone: true,
  imports: [FormsModule, ErrorAlertComponent, AsyncMultiSelectComponent],
  template: `
    <div>
      <div class="admin-header-row">
        <h1 class="admin-title">Услуги</h1>
        <button type="button" class="btn btn-primary" (click)="openCreate()">Добавить</button>
      </div>
      @if (error) { <app-error-alert [message]="error" /> }

      @if (showForm) {
        <div class="modal-overlay" (click)="closeForm()">
          <form class="modal card" (click)="$event.stopPropagation()" (ngSubmit)="save()">
            <h2>{{ editId ? 'Редактировать' : 'Новая' }} услуга</h2>
            <div class="form-group"><label>Название</label><input required [(ngModel)]="form.title" name="title" /></div>
            <div class="form-group"><label>Описание</label><textarea required rows="3" [(ngModel)]="form.description" name="description"></textarea></div>
            <div class="form-row">
              <div class="form-group"><label>Цена</label><input type="number" min="0" required [(ngModel)]="form.price" name="price" /></div>
              <div class="form-group"><label>Длительность</label><input required [(ngModel)]="form.duration" name="duration" /></div>
            </div>
            <app-async-multi-select label="Гости (M:N)" [selected]="form.guestIds ?? []" (selectedChange)="form.guestIds = $event" [loadOptions]="loadGuestOptions" />
            <app-async-multi-select label="Сотрудники (M:N)" [selected]="form.employeeIds ?? []" (selectedChange)="form.employeeIds = $event" [loadOptions]="loadEmployeeOptions" />
            <div class="btn-row">
              <button type="submit" class="btn btn-primary">Сохранить</button>
              <button type="button" class="btn btn-outline" (click)="closeForm()">Отмена</button>
            </div>
          </form>
        </div>
      }

      <table class="data-table">
        <thead><tr><th>ID</th><th>Название</th><th>Цена</th><th>Длительность</th><th>Гости</th><th>Сотрудники</th><th></th></tr></thead>
        <tbody>
          @for (s of items; track s.id) {
            <tr>
              <td>{{ s.id }}</td><td>{{ s.title }}</td><td>{{ s.price }}</td><td>{{ s.duration }}</td>
              <td>{{ s.guestIds?.join(', ') || '—' }}</td>
              <td>{{ s.employeeIds?.join(', ') || '—' }}</td>
              <td class="actions">
                <button type="button" class="btn btn-sm btn-outline" (click)="openEdit(s)">Изменить</button>
                <button type="button" class="btn btn-sm btn-danger" (click)="remove(s.id)">Удалить</button>
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
})
export class ServiceAdminComponent implements OnInit {
  items: Service[] = [];
  form: ServiceRequest = { ...empty };
  showForm = false;
  editId: number | null = null;
  error = '';

  loadGuestOptions = async () => {
    const p = await this.api.guest.list();
    return p.content.map((g) => ({ id: g.id, label: g.fullName }));
  };

  loadEmployeeOptions = async () => {
    const p = await this.api.employee.list();
    return p.content.map((e) => ({ id: e.id, label: e.fullName }));
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  openCreate(): void { this.editId = null; this.form = { ...empty }; this.showForm = true; }

  openEdit(s: Service): void {
    this.editId = s.id;
    this.form = { title: s.title, description: s.description, price: s.price, duration: s.duration, guestIds: s.guestIds ?? [], employeeIds: s.employeeIds ?? [] };
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; }

  async save(): Promise<void> {
    try {
      if (this.editId) await this.api.service.update(this.editId, this.form);
      else await this.api.service.create(this.form);
      this.closeForm();
      this.load();
    } catch (err) {
      this.error = err instanceof ApiError ? err.message : 'Ошибка';
    }
  }

  async remove(id: number): Promise<void> {
    if (!confirm('Удалить?')) return;
    await this.api.service.delete(id);
    this.load();
  }

  private async load(): Promise<void> {
    const data = await this.api.service.list();
    this.items = data.content;
  }
}
