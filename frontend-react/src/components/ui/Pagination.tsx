import type { Page } from '../../api/types';

interface PageInfo {
  page: number;
  totalPages: number;
  totalElements: number;
}

interface Props {
  pageInfo: PageInfo;
  onPageChange: (page: number) => void;
}

export function Pagination({ pageInfo, onPageChange }: Props) {
  const { page, totalPages, totalElements } = pageInfo;
  if (totalPages <= 1) return null;

  return (
    <div className="pagination">
      <button
        type="button"
        className="btn btn-sm btn-outline"
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
      >
        Назад
      </button>
      <span className="pagination-info">
        Страница {page + 1} из {totalPages} ({totalElements} записей)
      </span>
      <button
        type="button"
        className="btn btn-sm btn-outline"
        disabled={page >= totalPages - 1}
        onClick={() => onPageChange(page + 1)}
      >
        Вперёд
      </button>
    </div>
  );
}

export function pageFromResponse<T>(response: Page<T>) {
  return {
    content: response.content,
    page: response.number,
    totalPages: response.totalPages,
    totalElements: response.totalElements,
  };
}
