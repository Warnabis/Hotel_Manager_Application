import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ApiError } from '../../core/services/api-client.service';
import { ErrorAlertComponent } from '../../components/ui/alert.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, ErrorAlertComponent],
  template: `
    <div class="container page auth-page">
      <div class="auth-card card">
        <h1>Вход для гостей</h1>
        <p class="text-muted">Введите email и пароль</p>
        @if (error) { <app-error-alert [message]="error" /> }
        <form (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>Email</label>
            <input type="email" [(ngModel)]="email" name="email" required />
            @if (fieldErrors['email']) { <span class="field-error">{{ fieldErrors['email'] }}</span> }
          </div>
          <div class="form-group">
            <label>Пароль</label>
            <input type="password" [(ngModel)]="password" name="password" />
          </div>
          <button type="submit" class="btn btn-primary btn-block" [disabled]="submitting">
            {{ submitting ? 'Вход...' : 'Войти' }}
          </button>
        </form>
        <p class="auth-footer">Нет аккаунта? <a routerLink="/register">Зарегистрироваться</a></p>
        <p class="auth-footer"><a routerLink="/admin/login">Вход для администратора</a></p>
      </div>
    </div>
  `,
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  fieldErrors: Record<string, string> = {};
  submitting = false;
  redirect = '/profile';

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    this.route.queryParams.subscribe((p) => {
      this.redirect = p['redirect'] ?? '/profile';
    });
  }

  async onSubmit(): Promise<void> {
    this.error = '';
    this.fieldErrors = {};
    if (!this.email.includes('@')) {
      this.fieldErrors['email'] = 'Некорректный email';
      return;
    }
    this.submitting = true;
    try {
      await this.auth.loginGuest(this.email, this.password);
      this.router.navigateByUrl(this.redirect);
    } catch (err) {
      this.error = err instanceof ApiError ? err.message : 'Ошибка входа';
    } finally {
      this.submitting = false;
    }
  }
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, ErrorAlertComponent],
  template: `
    <div class="container page auth-page">
      <div class="auth-card card">
        <h1>Регистрация гостя</h1>
        @if (error) { <app-error-alert [message]="error" [validationErrors]="validationErrors" /> }
        <form (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>ФИО *</label>
            <input [(ngModel)]="fullName" name="fullName" required />
          </div>
          <div class="form-group">
            <label>Телефон *</label>
            <input [(ngModel)]="phoneNumber" name="phoneNumber" required />
          </div>
          <div class="form-group">
            <label>Email *</label>
            <input type="email" [(ngModel)]="email" name="email" required />
          </div>
          <div class="form-group">
            <label>Пароль *</label>
            <input type="password" [(ngModel)]="password" name="password" required minlength="6" />
          </div>
          <button type="submit" class="btn btn-primary btn-block" [disabled]="submitting">
            {{ submitting ? 'Регистрация...' : 'Зарегистрироваться' }}
          </button>
        </form>
        <p class="auth-footer">Уже есть аккаунт? <a routerLink="/login">Войти</a></p>
      </div>
    </div>
  `,
})
export class RegisterComponent {
  fullName = '';
  phoneNumber = '';
  email = '';
  password = '';
  error = '';
  validationErrors?: Record<string, string>;
  submitting = false;

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  async onSubmit(): Promise<void> {
    this.error = '';
    this.validationErrors = undefined;
    this.submitting = true;
    try {
      await this.auth.registerGuest({
        fullName: this.fullName,
        phoneNumber: this.phoneNumber,
        email: this.email,
        password: this.password,
      });
      this.router.navigate(['/profile']);
    } catch (err) {
      if (err instanceof ApiError) {
        this.error = err.message;
        this.validationErrors = err.validationErrors;
      } else {
        this.error = 'Ошибка регистрации';
      }
    } finally {
      this.submitting = false;
    }
  }
}

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [FormsModule, RouterLink, ErrorAlertComponent],
  template: `
    <div class="container page auth-page">
      <div class="auth-card card">
        <h1>Вход администратора</h1>
        <p class="text-muted">Введите пароль администратора</p>
        @if (error) { <app-error-alert [message]="error" /> }
        <form (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>Пароль</label>
            <input type="password" required [(ngModel)]="password" name="password" />
          </div>
          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading">
            {{ loading ? 'Вход...' : 'Войти в панель' }}
          </button>
        </form>
        <p class="auth-footer"><a routerLink="/">На главную</a></p>
      </div>
    </div>
  `,
})
export class AdminLoginComponent implements OnInit {
  password = '';
  error = '';
  loading = false;

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    if (this.auth.isAdmin) this.router.navigate(['/admin']);
  }

  async onSubmit(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      await this.auth.loginAdmin(this.password);
      this.router.navigate(['/admin']);
    } catch (err) {
      this.error = err instanceof ApiError ? err.message : 'Неверный пароль администратора';
    } finally {
      this.loading = false;
    }
  }
}
