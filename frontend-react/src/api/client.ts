import type { ErrorResponse } from './types';

const BASE = '/api';

const TOKEN_KEY = 'hotel_token';
const ROLE_KEY = 'hotel_role';

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string | null) {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
}

export function getRole(): string | null {
  return localStorage.getItem(ROLE_KEY);
}

export function setRole(role: string | null) {
  if (role) localStorage.setItem(ROLE_KEY, role);
  else localStorage.removeItem(ROLE_KEY);
}

export class ApiError extends Error {
  status: number;
  validationErrors?: Record<string, string>;

  constructor(status: number, message: string, validationErrors?: Record<string, string>) {
    super(message);
    this.status = status;
    this.validationErrors = validationErrors;
  }
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (res.ok) {
    if (res.status === 204) return undefined as T;
    try {
      const text = await res.text();
      if (!text) return undefined as T;
      return JSON.parse(text) as T;
    } catch {
      return undefined as T;
    }
  }
  let body: ErrorResponse | null = null;
  try {
    const text = await res.text();
    if (text) {
      body = JSON.parse(text);
    }
  } catch {
    /* empty */
  }
  throw new ApiError(
      res.status,
      body?.message ?? `HTTP ${res.status}`,
      body?.validationErrors,
  );
}

function buildQuery(params: Record<string, string | number | undefined>): string {
  const q = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== '') q.set(k, String(v));
  });
  const s = q.toString();
  return s ? `?${s}` : '';
}

function authHeaders(): HeadersInit {
  const token = getToken();
  const headers: HeadersInit = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

export const api = {
  get<T>(path: string, params: Record<string, string | number | undefined> = {}): Promise<T> {
    return fetch(`${BASE}${path}${buildQuery(params)}`, {
      headers: authHeaders(),
    }).then(handleResponse<T>);
  },
  post<T>(path: string, body: unknown): Promise<T> {
    return fetch(`${BASE}${path}`, {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify(body),
    }).then(handleResponse<T>);
  },
  put<T>(path: string, body: unknown): Promise<T> {
    return fetch(`${BASE}${path}`, {
      method: 'PUT',
      headers: authHeaders(),
      body: JSON.stringify(body),
    }).then(handleResponse<T>);
  },
  delete(path: string): Promise<void> {
    return fetch(`${BASE}${path}`, {
      method: 'DELETE',
      headers: authHeaders(),
    }).then(handleResponse<void>);
  },
};