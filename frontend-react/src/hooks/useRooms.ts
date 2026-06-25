import { useQuery } from '@tanstack/react-query';
import { roomApi } from '../api/services';

export function useRooms(filters: { type?: string; status?: string }) {
  return useQuery({
    queryKey: ['rooms', filters],
    queryFn: () => roomApi.list({
      type: filters.type || undefined,
      status: filters.status || undefined,
      size: 100,
    }),
  });
}
