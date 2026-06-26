import { Component, ElementRef, HostListener, Input, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MultiSelectOption } from '../../core/models/types';

@Component({
  selector: 'app-multi-select',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="form-group multi-select" [class.multi-select-open]="open">
      <label>{{ label }}</label>
      <button type="button" class="multi-select-trigger" (click)="open = !open" [disabled]="loading" [attr.aria-expanded]="open">
        {{ loading ? 'Загрузка...' : (selectedLabels.length ? selectedLabels.join(', ') : 'Выберите...') }}
      </button>
      @if (open) {
        <div class="multi-select-dropdown">
          <div class="multi-select-dropdown-header">
            <span>{{ label }}</span>
            <button type="button" class="multi-select-close" (click)="open = false" aria-label="Закрыть">×</button>
          </div>
          <div class="multi-select-options">
            @for (o of options; track o.id) {
              <label class="multi-select-option">
                <input type="checkbox" [checked]="selected.includes(o.id)" (change)="toggle(o.id)" />
                {{ o.label }}
              </label>
            }
            @if (!options.length) {
              <div class="text-muted multi-select-empty">Нет данных</div>
            }
          </div>
        </div>
      }
    </div>
  `,
})
export class MultiSelectComponent {
  @Input({ required: true }) label!: string;
  @Input() options: MultiSelectOption[] = [];
  @Input() selected: number[] = [];
  @Input() loading = false;
  @Output() selectedChange = new EventEmitter<number[]>();

  open = false;
  private el = inject(ElementRef);

  get selectedLabels(): string[] {
    return this.options.filter((o) => this.selected.includes(o.id)).map((o) => o.label);
  }

  toggle(id: number): void {
    const next = this.selected.includes(id)
      ? this.selected.filter((x) => x !== id)
      : [...this.selected, id];
    this.selectedChange.emit(next);
  }

  @HostListener('document:mousedown', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (this.open && !this.el.nativeElement.contains(event.target)) {
      this.open = false;
    }
  }
}

@Component({
  selector: 'app-async-multi-select',
  standalone: true,
  imports: [MultiSelectComponent],
  template: `
    <app-multi-select
      [label]="label"
      [options]="options"
      [selected]="selected"
      [loading]="loading"
      (selectedChange)="selectedChange.emit($event)"
    />
  `,
})
export class AsyncMultiSelectComponent implements OnInit {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) loadOptions!: () => Promise<MultiSelectOption[]>;
  @Input() selected: number[] = [];
  @Output() selectedChange = new EventEmitter<number[]>();

  options: MultiSelectOption[] = [];
  loading = true;

  ngOnInit(): void {
    this.loadOptions().then((opts) => {
      this.options = opts;
      this.loading = false;
    });
  }
}
