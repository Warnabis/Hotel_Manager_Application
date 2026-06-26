import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { Room } from '../../core/models/types';
import { ROOM_TYPE_LABELS, ROOM_STATUS_LABELS } from '../../core/utils/schemas';
import { ErrorAlertComponent } from '../../components/ui/alert.component';

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [FormsModule, RouterLink, DecimalPipe, ErrorAlertComponent],
  template: `
    <div class="container page">
      <div class="page-header">
        <h1>Номера отеля</h1>
        <p>Выберите подходящий номер для вашего пребывания</p>
        @if (returnUrl) {
          <div style="margin-top: 0.5rem">
            <button type="button" class="btn btn-sm btn-outline" (click)="goBack()">← Вернуться к бронированию</button>
          </div>
        }
      </div>

      <div class="filters-bar">
        <select [(ngModel)]="typeFilter" (ngModelChange)="updateFilter('type', $event)">
          <option value="">Все типы</option>
          <option value="Одноместный">Одноместный</option>
          <option value="Двухместный">Двухместный</option>
          <option value="Люкс">Люкс</option>
        </select>
        <select [(ngModel)]="statusFilter" (ngModelChange)="updateFilter('status', $event)">
          <option value="">Все статусы</option>
          <option value="Свободно">Свободно</option>
          <option value="Занято">Занято</option>
          <option value="Ремонт">Ремонт</option>
        </select>
        @if (typeFilter || statusFilter) {
          <button type="button" class="btn btn-sm btn-outline" (click)="clearFilters()">Сбросить фильтры</button>
        }
      </div>

      @if (error) {
        <app-error-alert [message]="error" />
      }
      @if (loading) {
        <div class="loading">Загрузка номеров...</div>
      }

      <div class="rooms-grid">
        @for (room of allRooms; track room.id) {
          <article class="room-card">
            <div class="room-image" [class]="'room-type-' + room.type.toLowerCase()">
              <span class="room-type-label">{{ roomTypeLabel(room) }}</span>
            </div>
            <div class="room-body">
              <div class="room-meta">
                <span class="badge" [class]="'badge-' + room.status">{{ roomStatusLabel(room) }}</span>
                <span>Этаж {{ room.floor }}</span>
              </div>
              <h3>{{ roomTypeLabel(room) }}</h3>
              <p class="room-price">{{ room.price }} BYN <span>/ ночь</span></p>
              @if (returnUrl) {
                <button class="btn btn-primary btn-block" (click)="selectRoom(room.id)">Выбрать</button>
              } @else {
                <a
                  class="btn btn-primary btn-block"
                  [routerLink]="['/booking']"
                  [queryParams]="{ roomId: room.id, checkIn: checkIn, duration: duration }"
                >Забронировать</a>
              }
            </div>
          </article>
        }
      </div>

      @if (!loading && !allRooms.length) {
        <div class="empty-state">Номера не найдены. Попробуйте изменить фильтры.</div>
      }
    </div>
  `,
})
export class RoomsComponent implements OnInit {
  allRooms: Room[] = [];
  loading = false;
  error = '';
  typeFilter = '';
  statusFilter = '';
  checkIn = '';
  duration = '1 ночь';
  returnUrl = '';
  selectedRooms: number[] = [];

  constructor(
    private api: ApiService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params: Record<string, any>) => {
      this.typeFilter = params['type'] ?? '';
      this.statusFilter = params['status'] ?? '';
      this.checkIn = params['checkIn'] ?? '';
      this.duration = params['duration'] ?? '1 ночь';
      this.returnUrl = params['returnUrl'] ?? '';

      if (params['selectedRooms']) {
        this.selectedRooms = (params['selectedRooms'] as string).split(',').map(Number);
      }

      this.loadRooms();
    });
  }

  roomTypeLabel(room: Room): string {
    return ROOM_TYPE_LABELS[room.type.toLowerCase()] ?? room.type;
  }

  roomStatusLabel(room: Room): string {
    return ROOM_STATUS_LABELS[room.status.toLowerCase()] ?? room.status;
  }

  updateFilter(key: string, value: string): void {
    const params = { ...this.route.snapshot.queryParams };
    if (value) params[key] = value;
    else delete params[key];
    this.router.navigate([], { queryParams: params });
  }

  clearFilters(): void {
    this.router.navigate([], { queryParams: {} });
  }

  selectRoom(id: number): void {
    if (!this.selectedRooms.includes(id)) {
      this.selectedRooms = [...this.selectedRooms, id];
    }
    this.goBack();
  }

  goBack(): void {
    const queryParams: Record<string, any> = {
      checkIn: this.checkIn,
      duration: this.duration
    };

    if (this.selectedRooms.length) {
      queryParams['selectedRooms'] = this.selectedRooms.join(',');
    }

    this.router.navigate([this.returnUrl || '/booking'], { queryParams });
  }

  private async loadRooms(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      const params: Record<string, string | number> = { size: 100 };
      if (this.typeFilter) params['type'] = this.typeFilter;
      if (this.statusFilter) params['status'] = this.statusFilter;
      const data = await this.api.room.list(params);
      this.allRooms = data.content;
    } catch (e) {
      this.error = e instanceof Error ? e.message : 'Ошибка загрузки';
    } finally {
      this.loading = false;
    }
  }
}
