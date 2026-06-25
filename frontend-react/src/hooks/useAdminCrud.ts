import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { ApiError } from '../api/client';
import type { Page } from '../api/types';
import { pageFromResponse } from '../components/ui/Pagination';

type ListFn<T> = (params: Record<string, string | number | undefined>) => Promise<Page<T>>;
type CreateFn<TReq, T> = (data: TReq) => Promise<T>;
type UpdateFn<TReq, T> = (id: number, data: TReq) => Promise<T>;
type DeleteFn = (id: number) => Promise<void>;

interface AdminCrudConfig<T, TReq> {
  queryKey: string;
  listFn: ListFn<T>;
  createFn: CreateFn<TReq, T>;
  updateFn: UpdateFn<TReq, T>;
  deleteFn: DeleteFn;
  pageSize?: number;
  filterParams?: Record<string, string | number | undefined>;
}

export function useAdminCrud<T extends { id: number }, TReq>({
  queryKey,
  listFn,
  createFn,
  updateFn,
  deleteFn,
  pageSize = 10,
  filterParams = {},
}: AdminCrudConfig<T, TReq>) {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');
  const [validationErrors, setValidationErrors] = useState<Record<string, string>>();

  const listQuery = useQuery({
    queryKey: [queryKey, page, filterParams],
    queryFn: () => listFn({ page, size: pageSize, ...filterParams }),
  });

  const pageData = listQuery.data ? pageFromResponse(listQuery.data) : null;

  const invalidate = () => queryClient.invalidateQueries({ queryKey: [queryKey] });

  const createMutation = useMutation({
    mutationFn: createFn,
    onSuccess: () => { setShowForm(false); setEditId(null); invalidate(); },
    onError: handleError,
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: TReq }) => updateFn(id, data),
    onSuccess: () => { setShowForm(false); setEditId(null); invalidate(); },
    onError: handleError,
  });

  const deleteMutation = useMutation({
    mutationFn: deleteFn,
    onSuccess: invalidate,
    onError: handleError,
  });

  function handleError(err: Error) {
    if (err instanceof ApiError) {
      setError(err.message);
      setValidationErrors(err.validationErrors);
    } else {
      setError('Произошла ошибка');
    }
  }

  const openCreate = () => {
    setEditId(null);
    setError('');
    setValidationErrors(undefined);
    setShowForm(true);
  };

  const openEdit = (item: T) => {
    setEditId(item.id);
    setError('');
    setValidationErrors(undefined);
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setEditId(null);
  };

  const save = async (data: TReq) => {
    setError('');
    setValidationErrors(undefined);
    if (editId) await updateMutation.mutateAsync({ id: editId, data });
    else await createMutation.mutateAsync(data);
  };

  const remove = async (id: number) => {
    if (!confirm('Удалить запись?')) return;
    setError('');
    await deleteMutation.mutateAsync(id);
  };

  return {
    items: (pageData?.content ?? []) as T[],
    pageInfo: pageData ? {
      page: pageData.page,
      totalPages: pageData.totalPages,
      totalElements: pageData.totalElements,
    } : { page: 0, totalPages: 0, totalElements: 0 },
    setPage,
    loading: listQuery.isLoading,
    error,
    validationErrors,
    setError,
    showForm,
    editId,
    openCreate,
    openEdit,
    closeForm,
    save,
    remove,
    isSaving: createMutation.isPending || updateMutation.isPending,
  };
}
