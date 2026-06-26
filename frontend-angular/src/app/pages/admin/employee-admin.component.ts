import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ApiError } from '../../core/services/api-client.service';
import { Employee, EmployeeRequest } from '../../core/models/types';
import { ErrorAlertComponent } from '../../components/ui/alert.component';
import { AsyncMultiSelectComponent } from '../../components/ui/multi-select.component';

const empty: EmployeeRequest = { fullName: '', experience: '', schedule: '', phoneNumber: '', positionIds: [], roomIds: [], serviceIds: [] };

@Component({
  selector: 'app-employee-admin',
  standalone: true,
  imports: [FormsModule, ErrorAlertComponent, AsyncMultiSelectComponent],
  template: `
    <div>
      <div class="admin-header-row">
        <h1 class="admin-title">Сотрудники</h1>
        <button type="button" class="btn btn-primary" (click)="openCreate()">Добавить</button>
      </div>
      @if (error) { <app-error-alert [message]="error" /> }

      @if (showForm) {
        <div class="modal-overlay" (click)="closeForm()">
          <form class="modal card modal-lg" (click)="$event.stopPropagation()" (ngSubmit)="save()">
            <h2>{{ editId ? 'Редактировать' : 'Новый' }} сотрудник</h2>
            <div class="form-group"><label>ФИО</label><input required [(ngModel)]="form.fullName" name="fullName" /></div>
            <div class="form-group"><label>Телефон</label><input required [(ngModel)]="form.phoneNumber" name="phoneNumber" /></div>
            <div class="form-group"><label>Опыт</label><input required [(ngModel)]="form.experience" name="experience" /></div>
            <div class="form-group"><label>График</label><input required [(ngModel)]="form.schedule" name="schedule" /></div>
            <app-async-multi-select label="Должности (M:N)" [selected]="form.positionIds ?? []" (selectedChange)="form.positionIds = $event" [loadOptions]="loadPositionOptions" />
            <app-async-multi-select label="Номера (M:N)" [selected]="form.roomIds ?? []" (selectedChange)="form.roomIds = $event" [loadOptions]="loadRoomOptions" />
            <app-async-multi-select label="Услуги (M:N)" [selected]="form.serviceIds ?? []" (selectedChange)="form.serviceIds = $event" [loadOptions]="loadServiceOptions" />
            <div class="btn-row">
              <button type="submit" class="btn btn-primary">Сохранить</button>
              <button type="button" class="btn btn-outline" (click)="closeForm()">Отмена</button>
            </div>
          </form>
        </div>
      }

      <table class="data-table">
        <thead><tr><th>ID</th><th>ФИО</th><th>Телефон</th><th>График</th><th>Должности</th><th>Номера</th><th>Услуги</th><th></th></tr></thead>
        <tbody>
          @for (e of items; track e.id) {
            <tr>
              <td>{{ e.id }}</td><td>{{ e.fullName }}</td><td>{{ e.phoneNumber }}</td><td>{{ e.schedule }}</td>
              <td>{{ e.positionIds?.join(', ') || '—' }}</td>
              <td>{{ e.roomIds?.join(', ') || '—' }}</td>
              <td>{{ e.serviceIds?.join(', ') || '—' }}</td>
              <td class="actions">
                <button type="button" class="btn btn-sm btn-outline" (click)="openEdit(e)">Изменить</button>
                <button type="button" class="btn btn-sm btn-danger" (click)="remove(e.id)">Удалить</button>
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
})
export class EmployeeAdminComponent implements OnInit {
  items: Employee[] = [];
  form: EmployeeRequest = { ...empty };
  showForm = false;
  editId: number | null = null;
  error = '';

  loadPositionOptions = async () => {
    const p = await this.api.position.list();
    return p.content.map((pos) => ({ id: pos.id, label: pos.title }));
  };

  loadRoomOptions = async () => {
    const p = await this.api.room.list();
    return p.content.map((r) => ({ id: r.id, label: `${r.type} #${r.id}` }));
  };

  loadServiceOptions = async () => {
    const p = await this.api.service.list();
    return p.content.map((s) => ({ id: s.id, label: s.title }));
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.load(); }

  openCreate(): void { this.editId = null; this.form = { ...empty }; this.showForm = true; }

  openEdit(e: Employee): void {
    this.editId = e.id;
    this.form = { fullName: e.fullName, experience: e.experience, schedule: e.schedule, phoneNumber: e.phoneNumber, positionIds: e.positionIds ?? [], roomIds: e.roomIds ?? [], serviceIds: e.serviceIds ?? [] };
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; }

  async save(): Promise<void> {
    try {
      if (this.editId) await this.api.employee.update(this.editId, this.form);
      else await this.api.employee.create(this.form);
      this.closeForm();
      this.load();
    } catch (err) {
      this.error = err instanceof ApiError ? err.message : 'Ошибка';
    }
  }

  async remove(id: number): Promise<void> {
    if (!confirm('Удалить?')) return;
    await this.api.employee.delete(id);
    this.load();
  }

  private async load(): Promise<void> {
    const data = await this.api.employee.list();
    this.items = data.content;
  }
}
