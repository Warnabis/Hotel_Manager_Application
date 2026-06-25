import { z } from 'zod';

export const guestSchema = z.object({
  fullName: z.string().min(1, 'ФИО обязательно'),
  phoneNumber: z.string().min(1, 'Телефон обязателен'),
  email: z.string().email('Некорректный email'),
  status: z.enum(['active', 'inactive', 'vip']),
  password: z.string().refine((val) => !val || val.length >= 6, { message: 'Минимум 6 символов' }).optional(),
  serviceIds: z.array(z.number()).optional(),
});

export type GuestFormValues = z.infer<typeof guestSchema>;

export const loginSchema = z.object({
  email: z.string().email('Некорректный email'),
  password: z.string().optional(),
});

export const registerSchema = z.object({
  fullName: z.string().min(1, 'ФИО обязательно'),
  phoneNumber: z.string().min(1, 'Телефон обязателен'),
  email: z.string().email('Некорректный email'),
  password: z.string().min(6, 'Пароль должен быть не менее 6 символов'),
});

export const ROOM_TYPE_LABELS: Record<string, string> = {
  standard: 'Стандарт',
  deluxe: 'Делюкс',
  suite: 'Люкс',
  family: 'Семейный',
};

export const ROOM_STATUS_LABELS: Record<string, string> = {
  available: 'Свободно',
  occupied: 'Занято',
  maintenance: 'Ремонт',
};

export function durationToNights(duration: string): number {
  if (duration.includes('недел')) return 7;
  const match = duration.match(/(\d+)/);
  return match ? Number(match[1]) : 1;
}
