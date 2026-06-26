import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-alert',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="alert alert-error">
      <div class="alert-header">
        <strong>{{ message }}</strong>
        @if (onClose.observed) {
          <button type="button" class="alert-close" (click)="onClose.emit()">×</button>
        }
      </div>
      @if (validationErrors) {
        <ul>
          @for (entry of validationEntries; track entry[0]) {
            <li>{{ entry[1] }}</li>
          }
        </ul>
      }
    </div>
  `,
})
export class ErrorAlertComponent {
  @Input({ required: true }) message!: string;
  @Input() validationErrors?: Record<string, string>;
  @Output() onClose = new EventEmitter<void>();

  get validationEntries(): [string, string][] {
    return this.validationErrors ? Object.entries(this.validationErrors) : [];
  }
}

@Component({
  selector: 'app-success-alert',
  standalone: true,
  template: `<div class="alert alert-success">{{ message }}</div>`,
})
export class SuccessAlertComponent {
  @Input({ required: true }) message!: string;
}
