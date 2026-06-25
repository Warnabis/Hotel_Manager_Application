import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import type { Page } from '../api/types';
import { pageFromResponse } from '../components/ui/Pagination';

export function usePaginatedList<T>(
  queryKey: string,
  listFn: (params: Record<string, string | number | undefined>) => Promise<Page<T>>,
  extraParams: Record<string, string | number | undefined> = {},
  pageSize = 10,
) {
  const [page, setPage] = useState(0);

  const query = useQuery({
    queryKey: [queryKey, page, extraParams],
    queryFn: () => listFn({ page, size: pageSize, ...extraParams }),
  });

  const pageData = query.data ? pageFromResponse(query.data) : null;

  return {
    items: (pageData?.content ?? []) as T[],
    pageInfo: pageData ? {
      page: pageData.page,
      totalPages: pageData.totalPages,
      totalElements: pageData.totalElements,
    } : { page: 0, totalPages: 0, totalElements: 0 },
    setPage,
    loading: query.isLoading,
    refetch: query.refetch,
  };
}
