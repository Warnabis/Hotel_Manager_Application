import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ApiError } from '../../core/services/api-client.service';
import { Position, PositionRequest } from '../../core/models/types';
import { ErrorAlertComponent } from '../../components/ui/alert.component';
import { AsyncMultiSelectComponent } from '../../components/ui/multi-select.component';

const empty: PositionRequest = { title: '', salary: 0, responsibilities: '', employeeIds: [] };

@Component({
  selector: 'app-position-admin',
  standalone: true,
  imports: [FormsModule, ErrorAlertComponent, AsyncMultiSelectComponent],
  template: `
    <div>
      <div class="admin-header-row">
        <h1 class="admin-title">Должности</h1>
        <button type="button" class="btn btn-primary" (click)="openCreate()">Добавить</button>
      </div>
      @if (error) { <app-error-alert [message]="error" /> }

      @if (showForm) {
        <div class="modal-overlay" (click)="closeForm()">
          <form class="modal card" (click)="$event.stopPropagation()" (ngSubmit)="save()">
            <h2>{{ editId ? 'Редактировать' : 'Новая' }} должность</h2>
            <div class="form-group"><label>Название</label><input required [(ngModel)]="form.title" name="title" /></div>
            <div class="form-group"><label>Зарплата</label><input type="number" min="0" required [(ngModel)]="form.salary" name="salary" /></div>
            <div class="form-group"><label>Обязанности</label><textarea required rows="4" [(ngModel)]="form.responsibilities" name="responsibilities"></textarea></div>
            <app-async-multi-select label="Сотрудники (M:N)" [selected]="form.employeeIds ?? []" (selectedChange)="form.employeeIds = $event" [loadOptions]="loadEmployeeOptions" />
            <div class="btn-row">
              <button type="submit" class="btn btn-primary">Сохранить</button>
              <button type="button" class="btn btn-outline" (click)="closeForm()">Отмена</button>
            </div>
          </form>
        </div>
      }

      <table class="data-table">
        <thead><tr><th>ID</th><th>Название</th><th>Зарплата</th><th>Обязанности</th><th>Сотрудники</th><th></th></tr></thead>
        <tbody>
          @for (p of items; track p.id) {
            <tr>
              <td>{{ p.id }}</td><td>{{ p.title }}</td><td>{{ p.salary }} BYN</td>
              <td class="truncate">{{ p.responsibilities }}</td>
              <td>{{ p.employeeIds?.join(', ') || '—' }}</td>
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
export class PositionAdminComponent implements OnInit {
  items: Position[] = [];
  form: PositionRequest = { ...empty };
  showForm = false;
  editId: number | null = null;
  error = '';

  loadEmployeeOptions = async () => {
    const p = await this.api.employee.list();
    return p.content.map((e) => ({ id: e.id, label: e.fullName }));
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  openCreate(): void { this.editId = null; this.form = { ...empty }; this.showForm = true; }

  openEdit(p: Position): void {
    this.editId = p.id;
    this.form = { title: p.title, salary: p.salary, responsibilities: p.responsibilities, employeeIds: p.employeeIds ?? [] };
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; }

  async save(): Promise<void> {
    try {
      if (this.editId) await this.api.position.update(this.editId, this.form);
      else await this.api.position.create(this.form);
      this.closeForm();
      this.load();
    } catch (err) {
      this.error = err instanceof ApiError ? err.message : 'Ошибка';
    }
  }

  async remove(id: number): Promise<void> {
    if (!confirm('Удалить?')) return;
    await this.api.position.delete(id);
    this.load();
  }

  private async load(): Promise<void> {
    const data = await this.api.position.list();
    this.items = data.content;
  }
}
