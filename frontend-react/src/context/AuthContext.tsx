import { createContext, useContext, useState, useCallback, type ReactNode } from 'react';
import type { Guest } from '../api/types';
import { authApi } from '../api/services';
import { setToken, setRole, getToken, getRole } from '../api/client';

interface AuthState {
  guest: Guest | null;
  isAdmin: boolean;
  loginGuest: (email: string, password?: string) => Promise<void>;
  registerGuest: (data: { fullName: string; phoneNumber: string; email: string; password: string }) => Promise<Guest>;
  loginAdmin: (password: string) => Promise<boolean>;
  logout: () => void;
  refreshGuest: () => Promise<void>;
  setGuest: (guest: Guest | null) => void;
}

const AuthContext = createContext<AuthState | null>(null);

const GUEST_KEY = 'hotel_guest';

function persistGuest(guest: Guest | null) {
  if (guest) localStorage.setItem(GUEST_KEY, JSON.stringify(guest));
  else localStorage.removeItem(GUEST_KEY);
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [guest, setGuestState] = useState<Guest | null>(() => {
    const raw = localStorage.getItem(GUEST_KEY);
    return raw ? JSON.parse(raw) : null;
  });
  const [isAdmin, setIsAdmin] = useState(() => getRole() === 'ADMIN' && !!getToken());

  const setGuest = useCallback((g: Guest | null) => {
    setGuestState(g);
    persistGuest(g);
  }, []);

  const applyAuth = useCallback((token: string, role: string, g?: Guest | null) => {
    setToken(token);
    setRole(role);
    setIsAdmin(role === 'ADMIN');
    if (role === 'GUEST' && g) {
      setGuest(g);
    } else if (role === 'ADMIN') {
      setGuest(null);
    }
  }, [setGuest]);

  const loginGuest = useCallback(async (email: string, password?: string) => {
    const res = await authApi.login({ email, password });
    applyAuth(res.token, res.role, res.guest ?? null);
  }, [applyAuth]);

  const registerGuest = useCallback(async (data: {
    fullName: string;
    phoneNumber: string;
    email: string;
    password: string;
  }) => {
    const res = await authApi.register(data);
    if (!res.guest) throw new Error('Ошибка регистрации');
    applyAuth(res.token, res.role, res.guest);
    return res.guest;
  }, [applyAuth]);

  const loginAdmin = useCallback(async (password: string) => {
    const res = await authApi.adminLogin(password);
    applyAuth(res.token, res.role);
    return true;
  }, [applyAuth]);

  const logout = useCallback(() => {
    setGuest(null);
    setIsAdmin(false);
    setToken(null);
    setRole(null);
  }, [setGuest]);

  const refreshGuest = useCallback(async () => {
    if (!getToken() || getRole() !== 'GUEST') return;
    const profile = await authApi.me();
    setGuest(profile);
  }, [setGuest]);

  return (
    <AuthContext.Provider value={{
      guest, isAdmin, loginGuest, registerGuest, loginAdmin, logout, refreshGuest, setGuest,
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
