import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Guest } from '../models/types';
import { ApiService } from './api.service';
import { ApiClientService } from './api-client.service';

const GUEST_KEY = 'hotel_guest';

@Injectable({ providedIn: 'root' })
export class AuthService {
  guest: Guest | null = this.loadGuest();
  isAdmin = false;

  constructor(
    private api: ApiService,
    private apiClient: ApiClientService,
    private router: Router,
  ) {
    this.isAdmin = this.apiClient.getRole() === 'ADMIN' && !!this.apiClient.getToken();
  }

  private loadGuest(): Guest | null {
    const raw = localStorage.getItem(GUEST_KEY);
    return raw ? JSON.parse(raw) : null;
  }

  private persistGuest(guest: Guest | null): void {
    if (guest) localStorage.setItem(GUEST_KEY, JSON.stringify(guest));
    else localStorage.removeItem(GUEST_KEY);
  }

  setGuest(guest: Guest | null): void {
    this.guest = guest;
    this.persistGuest(guest);
  }

  private applyAuth(token: string, role: string, guest?: Guest | null): void {
    this.apiClient.setToken(token);
    this.apiClient.setRole(role);
    this.isAdmin = role === 'ADMIN';
    if (role === 'GUEST' && guest) this.setGuest(guest);
    else if (role === 'ADMIN') this.setGuest(null);
  }

  async loginGuest(email: string, password?: string): Promise<void> {
    const res = await this.api.auth.login({ email, password });
    this.applyAuth(res.token, res.role, res.guest ?? null);
  }

  async registerGuest(data: { fullName: string; phoneNumber: string; email: string; password: string }): Promise<Guest> {
    const res = await this.api.auth.register(data);
    if (!res.guest) throw new Error('Ошибка регистрации');
    this.applyAuth(res.token, res.role, res.guest);
    return res.guest;
  }

  async loginAdmin(password: string): Promise<boolean> {
    const res = await this.api.auth.adminLogin(password);
    this.applyAuth(res.token, res.role);
    return true;
  }

  logout(): void {
    this.setGuest(null);
    this.isAdmin = false;
    this.apiClient.setToken(null);
    this.apiClient.setRole(null);
    if (this.router.url === '/profile') {
      this.router.navigate(['/']);
    }
  }

  async refreshGuest(): Promise<void> {
    if (!this.apiClient.getToken() || this.apiClient.getRole() !== 'GUEST') return;
    const profile = await this.api.auth.me();
    this.setGuest(profile);
  }
}
