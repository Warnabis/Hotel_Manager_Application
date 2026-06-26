import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

const FEATURES = [
  { title: 'Комфортные номера', desc: 'Стандарт, люкс и семейные номера на любой вкус' },
  { title: 'SPA и услуги', desc: 'Массаж, завтрак в номер, трансфер и многое другое' },
  { title: 'Удобная оплата', desc: 'Карта, наличные, онлайн-оплата при бронировании' },
  { title: 'Онлайн-бронирование', desc: 'Мгновенное подтверждение брони без звонков' },
];

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <section class="hero">
      <div class="hero-overlay"></div>
      <div class="container hero-content">
        <h1>Найдите идеальный номер для вашего отдыха</h1>
        <p>Комфортабельные номера, SPA-услуги и профессиональный сервис</p>
        <form class="search-bar" (ngSubmit)="handleSearch()">
          <div class="search-field">
            <label>Дата заезда</label>
            <input type="date" [(ngModel)]="checkIn" name="checkIn" />
          </div>
          <div class="search-field">
            <label>Длительность</label>
            <select [(ngModel)]="duration" name="duration">
              <option>1 ночь</option>
              <option>2 ночи</option>
              <option>3 ночи</option>
              <option>1 неделя</option>
            </select>
          </div>
          <div class="search-field">
            <label>Гости</label>
            <select [(ngModel)]="guests" name="guests">
              @for (n of guestOptions; track n) {
                <option [ngValue]="n">{{ n }} {{ n === 1 ? 'гость' : 'гостей' }}</option>
              }
            </select>
          </div>
          <button type="submit" class="btn btn-accent btn-search">Найти номера</button>
        </form>
      </div>
    </section>

    <section class="section container">
      <h2 class="section-title">Почему выбирают нас</h2>
      <div class="features-grid">
        @for (f of features; track f.title; let i = $index) {
          <div class="feature-card">
            <span class="feature-index">{{ (i + 1).toString().padStart(2, '0') }}</span>
            <h3>{{ f.title }}</h3>
            <p>{{ f.desc }}</p>
          </div>
        }
      </div>
    </section>

    <section class="section section-alt">
      <div class="container cta-block">
        <div>
          <h2>Готовы забронировать?</h2>
          <p>Выберите номер и оформите бронирование за несколько минут</p>
        </div>
        <a routerLink="/booking" class="btn btn-accent btn-lg">Забронировать сейчас</a>
      </div>
    </section>
  `,
})
export class HomeComponent {
  features = FEATURES;
  guestOptions = [1, 2, 3, 4, 5, 6];
  checkIn = '';
  duration = '1 ночь';
  guests = 2;

  constructor(private router: Router) {}

  handleSearch(): void {
    const params: Record<string, string> = { guests: String(this.guests) };
    if (this.checkIn) params['checkIn'] = this.checkIn;
    if (this.duration) params['duration'] = this.duration;
    this.router.navigate(['/rooms'], { queryParams: params });
  }
}
