import { Link, useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { roomApi } from '../../api/services';
import { ErrorAlert } from '../../components/ui/Alert';
import { ROOM_TYPE_LABELS, ROOM_STATUS_LABELS } from '../../lib/schemas';

export function RoomsPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const typeFilter = searchParams.get('type') ?? '';
  const statusFilter = searchParams.get('status') ?? '';

  const { data, isLoading, error } = useQuery({
    queryKey: ['rooms-all', typeFilter, statusFilter],
    queryFn: () => {
      const params: any = { size: 100 };
      if (typeFilter) params.type = typeFilter;
      if (statusFilter) params.status = statusFilter;
      return roomApi.list(params);
    },
  });

  const allRooms = data?.content ?? [];

  const checkIn = searchParams.get('checkIn') ?? '';
  const duration = searchParams.get('duration') ?? '1 ночь';

  const updateFilter = (key: string, value: string) => {
    const next = new URLSearchParams(searchParams);
    if (value) {
      next.set(key, value);
    } else {
      next.delete(key);
    }
    setSearchParams(next);
  };

  const clearFilters = () => {
    setSearchParams({});
  };

  return (
      <div className="container page">
        <div className="page-header">
          <h1>Номера отеля</h1>
          <p>Выберите подходящий номер для вашего пребывания</p>
        </div>

        <div className="filters-bar">
          <select value={typeFilter} onChange={(e) => updateFilter('type', e.target.value)}>
            <option value="">Все типы</option>
            <option value="Одноместный">Одноместный</option>
            <option value="Двухместный">Двухместный</option>
            <option value="Люкс">Люкс</option>
          </select>
          <select value={statusFilter} onChange={(e) => updateFilter('status', e.target.value)}>
            <option value="">Все статусы</option>
            <option value="Свободно">Свободно</option>
            <option value="Занято">Занято</option>
            <option value="Ремонт">Ремонт</option>
          </select>
          {(typeFilter || statusFilter) && (
              <button onClick={clearFilters} className="btn btn-sm btn-outline">
                Сбросить фильтры
              </button>
          )}
        </div>

        {error && <ErrorAlert message={(error as Error).message} />}
        {isLoading && <div className="loading">Загрузка номеров...</div>}

        <div className="rooms-grid">
          {allRooms.map((room) => {
            const roomType = ROOM_TYPE_LABELS[room.type.toLowerCase()] ?? room.type;
            const roomStatus = ROOM_STATUS_LABELS[room.status.toLowerCase()] ?? room.status;
            return (
                <article key={room.id} className="room-card">
                  <div className={`room-image room-type-${room.type.toLowerCase()}`}>
                    <span className="room-type-label">{roomType}</span>
                  </div>
                  <div className="room-body">
                    <div className="room-meta">
                  <span className={`badge badge-${room.status}`}>
                    {roomStatus}
                  </span>
                      <span>Этаж {room.floor}</span>
                    </div>
                    <h3>{roomType}</h3>
                    <p className="room-price">{room.price.toLocaleString('ru-RU')} BYN <span>/ ночь</span></p>
                    <Link
                        to={`/booking?roomId=${room.id}&checkIn=${checkIn}&duration=${encodeURIComponent(duration)}`}
                        className="btn btn-primary btn-block"
                    >
                      Забронировать
                    </Link>
                  </div>
                </article>
            );
          })}
        </div>

        {!isLoading && allRooms.length === 0 && (
            <div className="empty-state">Номера не найдены. Попробуйте изменить фильтры.</div>
        )}
      </div>
  );
}