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

export function pageFromResponse<T>(response: { content: T[]; number: number; totalPages: number; totalElements: number }) {
  return {
    content: response.content,
    page: response.number,
    totalPages: response.totalPages,
    totalElements: response.totalElements,
  };
}
