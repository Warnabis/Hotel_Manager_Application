import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ApiError } from '../../core/services/api-client.service';
import { Guest, GuestRequest } from '../../core/models/types';
import { pageFromResponse } from '../../core/utils/schemas';
import { ErrorAlertComponent } from '../../components/ui/alert.component';
import { AsyncMultiSelectComponent } from '../../components/ui/multi-select.component';
import { PaginationComponent } from '../../components/ui/pagination.component';

@Component({
  selector: 'app-guest-admin',
  standalone: true,
  imports: [FormsModule, ErrorAlertComponent, AsyncMultiSelectComponent, PaginationComponent],
  template: `
    <div>
      <div class="admin-header-row">
        <h1 class="admin-title">Гости</h1>
        <button type="button" class="btn btn-primary" (click)="openCreate()">Добавить</button>
      </div>
      <input class="filter-input" placeholder="Поиск по имени..." [(ngModel)]="filter" (ngModelChange)="onFilterChange()" />
      @if (error) { <app-error-alert [message]="error" [validationErrors]="validationErrors" /> }

      @if (showForm) {
        <div class="modal-overlay" (click)="closeForm()">
          <form class="modal card" (click)="$event.stopPropagation()" (ngSubmit)="save()">
            <h2>{{ editId ? 'Редактировать' : 'Новый' }} гость</h2>
            <div class="form-group"><label>ФИО</label><input [(ngModel)]="form.fullName" name="fullName" required /></div>
            <div class="form-group"><label>Телефон</label><input [(ngModel)]="form.phoneNumber" name="phoneNumber" required /></div>
            <div class="form-group"><label>Email</label><input type="email" [(ngModel)]="form.email" name="email" required /></div>
            <div class="form-group">
              <label>Пароль {{ editId ? '(оставьте пустым, чтобы не менять)' : '' }}</label>
              <input type="password" [(ngModel)]="form.password" name="password" />
            </div>
            <div class="form-group">
              <label>Статус</label>
              <select [(ngModel)]="form.status" name="status">
                <option value="Зарегистрирован">Зарегистрирован</option>
                <option value="Постоянный гость">Постоянный гость</option>
                <option value="VIP">VIP</option>
              </select>
            </div>
            <app-async-multi-select label="Услуги" [selected]="form.serviceIds ?? []" (selectedChange)="form.serviceIds = $event"
                                    [loadOptions]="loadServiceOptions" />
            <div class="btn-row">
              <button type="submit" class="btn btn-primary" [disabled]="isSaving">{{ isSaving ? 'Сохранение...' : 'Сохранить' }}</button>
              <button type="button" class="btn btn-outline" (click)="closeForm()">Отмена</button>
            </div>
          </form>
        </div>
      }

      @if (loading) { <div class="loading">Загрузка...</div> }
      <table class="data-table">
        <thead><tr><th>ID</th><th>ФИО</th><th>Email</th><th>Телефон</th><th>Статус</th><th>Услуги</th><th></th></tr></thead>
        <tbody>
          @for (g of items; track g.id) {
            <tr>
              <td>{{ g.id }}</td><td>{{ g.fullName }}</td><td>{{ g.email }}</td><td>{{ g.phoneNumber }}</td>
              <td><span class="badge">{{ g.status }}</span></td>
              <td>{{ g.serviceIds?.join(', ') || '—' }}</td>
              <td class="actions">
                <button type="button" class="btn btn-sm btn-outline" (click)="openEdit(g)">Изменить</button>
                <button type="button" class="btn btn-sm btn-danger" (click)="remove(g.id)">Удалить</button>
              </td>
            </tr>
          }
        </tbody>
      </table>
      <app-pagination [pageInfo]="pageInfo" (pageChange)="setPage($event)" />
    </div>
  `,
})
export class GuestAdminComponent implements OnInit {
  items: Guest[] = [];
  pageInfo = { page: 0, totalPages: 0, totalElements: 0 };
  filter = '';
  loading = false;
  error = '';
  validationErrors?: Record<string, string>;
  showForm = false;
  editId: number | null = null;
  isSaving = false;
  form: GuestRequest & { password?: string } = this.emptyForm();

  loadServiceOptions = async () => {
    const p = await this.api.service.list({ size: 100 });
    return p.content.map((s) => ({ id: s.id, label: s.title }));
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  emptyForm(): GuestRequest & { password?: string } {
    return { fullName: '', phoneNumber: '', email: '', status: 'Зарегистрирован', password: '', serviceIds: [] };
  }

  onFilterChange(): void {
    this.pageInfo.page = 0;
    this.load();
  }

  setPage(page: number): void {
    this.pageInfo.page = page;
    this.load();
  }

  openCreate(): void {
    this.editId = null;
    this.form = this.emptyForm();
    this.error = '';
    this.validationErrors = undefined;
    this.showForm = true;
  }

  openEdit(g: Guest): void {
    this.editId = g.id;
    this.form = { fullName: g.fullName, phoneNumber: g.phoneNumber, email: g.email, status: g.status, password: '', serviceIds: g.serviceIds ?? [] };
    this.error = '';
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editId = null;
  }

  async save(): Promise<void> {
    this.isSaving = true;
    this.error = '';
    this.validationErrors = undefined;
    const payload: GuestRequest = {
      fullName: this.form.fullName,
      phoneNumber: this.form.phoneNumber,
      email: this.form.email,
      status: this.form.status,
      serviceIds: this.form.serviceIds ?? [],
    };
    if (this.form.password) payload.password = this.form.password;
    try {
      if (this.editId) await this.api.guest.update(this.editId, payload);
      else await this.api.guest.create(payload);
      this.closeForm();
      this.load();
    } catch (err) {
      if (err instanceof ApiError) {
        this.error = err.message;
        this.validationErrors = err.validationErrors;
      } else this.error = 'Произошла ошибка';
    } finally {
      this.isSaving = false;
    }
  }

  async remove(id: number): Promise<void> {
    if (!confirm('Удалить запись?')) return;
    await this.api.guest.delete(id);
    this.load();
  }

  private async load(): Promise<void> {
    this.loading = true;
    const data = await this.api.guest.list({ page: this.pageInfo.page, size: 10, fullName: this.filter || undefined });
    const parsed = pageFromResponse(data);
    this.items = parsed.content;
    this.pageInfo = { page: parsed.page, totalPages: parsed.totalPages, totalElements: parsed.totalElements };
    this.loading = false;
  }
}
