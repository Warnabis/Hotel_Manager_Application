import { Component, Input, Output, EventEmitter } from '@angular/core';
import { PageInfo } from '../../core/models/types';

@Component({
  selector: 'app-pagination',
  standalone: true,
  template: `
    @if (pageInfo.totalPages > 1) {
      <div class="pagination">
        <button type="button" class="btn btn-sm btn-outline" [disabled]="pageInfo.page === 0" (click)="pageChange.emit(pageInfo.page - 1)">
          Назад
        </button>
        <span class="pagination-info">
          Страница {{ pageInfo.page + 1 }} из {{ pageInfo.totalPages }} ({{ pageInfo.totalElements }} записей)
        </span>
        <button type="button" class="btn btn-sm btn-outline" [disabled]="pageInfo.page >= pageInfo.totalPages - 1" (click)="pageChange.emit(pageInfo.page + 1)">
          Вперёд
        </button>
      </div>
    }
  `,
})
export class PaginationComponent {
  @Input({ required: true }) pageInfo!: PageInfo;
  @Output() pageChange = new EventEmitter<number>();
}
