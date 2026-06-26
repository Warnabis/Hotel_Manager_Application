import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { ErrorResponse } from '../models/types';

const BASE = '/api';
const TOKEN_KEY = 'hotel_token';
const ROLE_KEY = 'hotel_role';

export class ApiError extends Error {
  status: number;
  validationErrors?: Record<string, string>;

  constructor(status: number, message: string, validationErrors?: Record<string, string>) {
    super(message);
    this.status = status;
    this.validationErrors = validationErrors;
  }
}

@Injectable({ providedIn: 'root' })
export class ApiClientService {
  constructor(private http: HttpClient) {}

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  setToken(token: string | null): void {
    if (token) localStorage.setItem(TOKEN_KEY, token);
    else localStorage.removeItem(TOKEN_KEY);
  }

  getRole(): string | null {
    return localStorage.getItem(ROLE_KEY);
  }

  setRole(role: string | null): void {
    if (role) localStorage.setItem(ROLE_KEY, role);
    else localStorage.removeItem(ROLE_KEY);
  }

  private authHeaders(): HttpHeaders {
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const token = this.getToken();
    if (token) headers = headers.set('Authorization', `Bearer ${token}`);
    return headers;
  }

  private buildParams(params: Record<string, string | number | undefined>): HttpParams {
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== '') httpParams = httpParams.set(k, String(v));
    });
    return httpParams;
  }

  private async handleError(err: unknown): Promise<never> {
    if (err instanceof HttpErrorResponse) {
      const body = err.error as ErrorResponse | null;
      throw new ApiError(
        err.status,
        body?.message ?? `HTTP ${err.status}`,
        body?.validationErrors,
      );
    }
    throw err;
  }

  async get<T>(path: string, params: Record<string, string | number | undefined> = {}): Promise<T> {
    try {
      return await firstValueFrom(
        this.http.get<T>(`${BASE}${path}`, {
          headers: this.authHeaders(),
          params: this.buildParams(params),
        }),
      );
    } catch (err) {
      return this.handleError(err);
    }
  }

  async post<T>(path: string, body: unknown): Promise<T> {
    try {
      return await firstValueFrom(
        this.http.post<T>(`${BASE}${path}`, body, { headers: this.authHeaders() }),
      );
    } catch (err) {
      return this.handleError(err);
    }
  }

  async put<T>(path: string, body: unknown): Promise<T> {
    try {
      return await firstValueFrom(
        this.http.put<T>(`${BASE}${path}`, body, { headers: this.authHeaders() }),
      );
    } catch (err) {
      return this.handleError(err);
    }
  }

  async delete(path: string): Promise<void> {
    try {
      await firstValueFrom(
        this.http.delete<void>(`${BASE}${path}`, { headers: this.authHeaders() }),
      );
    } catch (err) {
      return this.handleError(err);
    }
  }
}
