import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ApiError } from '../../core/services/api-client.service';
import { Room, RoomRequest } from '../../core/models/types';
import { pageFromResponse } from '../../core/utils/schemas';
import { ErrorAlertComponent } from '../../components/ui/alert.component';
import { AsyncMultiSelectComponent } from '../../components/ui/multi-select.component';
import { PaginationComponent } from '../../components/ui/pagination.component';

const empty: RoomRequest = { floor: 1, type: 'одноместный', status: 'свободно', price: 100, employeeIds: [] };

@Component({
  selector: 'app-room-admin',
  standalone: true,
  imports: [FormsModule, ErrorAlertComponent, AsyncMultiSelectComponent, PaginationComponent],
  template: `
    <div>
      <div class="admin-header-row">
        <h1 class="admin-title">Номера</h1>
        <button type="button" class="btn btn-primary" (click)="openCreate()">Добавить</button>
      </div>
      @if (error) { <app-error-alert [message]="error" /> }

      @if (showForm) {
        <div class="modal-overlay" (click)="closeForm()">
          <form class="modal card" (click)="$event.stopPropagation()" (ngSubmit)="save()">
            <h2>{{ editId ? 'Редактировать' : 'Новый' }} номер</h2>
            <div class="form-row">
              <div class="form-group"><label>Этаж</label><input type="number" min="1" required [(ngModel)]="form.floor" name="floor" /></div>
              <div class="form-group"><label>Цена</label><input type="number" min="0" required [(ngModel)]="form.price" name="price" /></div>
            </div>
            <div class="form-group"><label>Тип</label>
              <select [(ngModel)]="form.type" name="type">
                <option value="одноместный">одноместный</option>
                <option value="двухместный">двухместный</option>
                <option value="люкс">люкс</option>
              </select>
            </div>
            <div class="form-group"><label>Статус</label>
              <select [(ngModel)]="form.status" name="status">
                <option value="свободно">свободно</option>
                <option value="занято">занято</option>
                <option value="ремонт">ремонт</option>
              </select>
            </div>
            <app-async-multi-select label="Сотрудники (обслуживание)" [selected]="form.employeeIds ?? []" (selectedChange)="form.employeeIds = $event"
                                    [loadOptions]="loadEmployeeOptions" />
            <div class="btn-row">
              <button type="submit" class="btn btn-primary" [disabled]="isSaving">Сохранить</button>
              <button type="button" class="btn btn-outline" (click)="closeForm()">Отмена</button>
            </div>
          </form>
        </div>
      }

      <table class="data-table">
        <thead><tr><th>ID</th><th>Этаж</th><th>Тип</th><th>Статус</th><th>Цена</th><th></th></tr></thead>
        <tbody>
          @for (r of items; track r.id) {
            <tr>
              <td>{{ r.id }}</td><td>{{ r.floor }}</td><td>{{ r.type }}</td>
              <td><span class="badge" [class]="'badge-' + r.status">{{ r.status }}</span></td>
              <td>{{ r.price }} BYN</td>
              <td class="actions">
                <button type="button" class="btn btn-sm btn-outline" (click)="openEdit(r)">Изменить</button>
                <button type="button" class="btn btn-sm btn-danger" (click)="remove(r.id)">Удалить</button>
              </td>
            </tr>
          }
        </tbody>
      </table>
      <app-pagination [pageInfo]="pageInfo" (pageChange)="setPage($event)" />
    </div>
  `,
})
export class RoomAdminComponent implements OnInit {
  items: Room[] = [];
  pageInfo = { page: 0, totalPages: 0, totalElements: 0 };
  form: RoomRequest = { ...empty };
  showForm = false;
  editId: number | null = null;
  error = '';
  isSaving = false;

  loadEmployeeOptions = async () => {
    const p = await this.api.employee.list({ size: 100 });
    return p.content.map((e) => ({ id: e.id, label: e.fullName }));
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  setPage(page: number): void { this.pageInfo.page = page; this.load(); }

  openCreate(): void { this.editId = null; this.form = { ...empty }; this.showForm = true; }

  openEdit(r: Room): void {
    this.editId = r.id;
    this.form = { floor: r.floor, type: r.type, status: r.status, price: r.price, employeeIds: [] };
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; this.editId = null; }

  async save(): Promise<void> {
    this.isSaving = true;
    try {
      if (this.editId) await this.api.room.update(this.editId, this.form);
      else await this.api.room.create(this.form);
      this.closeForm();
      this.load();
    } catch (err) {
      this.error = err instanceof ApiError ? err.message : 'Ошибка';
    } finally {
      this.isSaving = false;
    }
  }

  async remove(id: number): Promise<void> {
    if (!confirm('Удалить запись?')) return;
    await this.api.room.delete(id);
    this.load();
  }

  private async load(): Promise<void> {
    const data = await this.api.room.list({ page: this.pageInfo.page, size: 10 });
    const parsed = pageFromResponse(data);
    this.items = parsed.content;
    this.pageInfo = { page: parsed.page, totalPages: parsed.totalPages, totalElements: parsed.totalElements };
  }
}
