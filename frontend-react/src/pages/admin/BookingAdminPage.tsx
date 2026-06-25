import { useEffect, useState } from 'react';
import { bookingApi, guestApi, paymentApi, roomApi } from '../../api/services';
import type { Booking, BookingRequest } from '../../api/types';
import { ApiError } from '../../api/client';
import { ErrorAlert } from '../../components/ui/Alert';
import { AsyncMultiSelect } from '../../components/ui/MultiSelect';
import { usePaginatedList } from '../../hooks/usePaginatedList';
import { Pagination } from '../../components/ui/Pagination';

const empty: BookingRequest = {
  price: 100, status: 'confirmed', checkInDate: new Date().toISOString().slice(0, 10), duration: '1 ночь',
  guestId: undefined, roomIds: [], paymentIds: [],
};

export function BookingAdminPage() {
  const { items, loading, refetch, pageInfo, setPage } = usePaginatedList(
    'admin-bookings',
    bookingApi.list,
    { sort: 'checkInDate,desc' },
  );
  const [form, setForm] = useState<BookingRequest>(empty);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');
  const [guestOptions, setGuestOptions] = useState<{ id: number; label: string }[]>([]);

  useEffect(() => {
    guestApi.list({ size: 100 }).then((p) => setGuestOptions(p.content.map((g) => ({ id: g.id, label: `${g.fullName} (${g.email})` }))));
  }, []);

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editId) await bookingApi.update(editId, form);
      else await bookingApi.create(form);
      setShowForm(false);
      refetch();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Ошибка');
    }
  };

  const openEdit = (b: Booking) => {
    setEditId(b.id);
    setForm({
      price: b.price, status: b.status, checkInDate: b.checkInDate, duration: b.duration,
      guestId: b.guest?.id, roomIds: b.rooms?.map((r) => r.id) ?? [], paymentIds: b.payments?.map((p) => p.id) ?? [],
    });
    setShowForm(true);
  };

  return (
    <div>
      <div className="admin-header-row">
        <h1 className="admin-title">Бронирования</h1>
        <button type="button" className="btn btn-primary" onClick={() => { setEditId(null); setForm(empty); setShowForm(true); }}>Добавить</button>
      </div>
      {error && <ErrorAlert message={error} />}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <form className="modal card modal-lg" onClick={(e) => e.stopPropagation()} onSubmit={save}>
            <h2>{editId ? 'Редактировать' : 'Новое'} бронирование</h2>
            <div className="form-group"><label>Гость (N:1)</label>
              <select value={form.guestId ?? ''} onChange={(e) => setForm({ ...form, guestId: e.target.value ? +e.target.value : undefined })}>
                <option value="">— выберите —</option>
                {guestOptions.map((g) => <option key={g.id} value={g.id}>{g.label}</option>)}
              </select>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Дата заезда</label><input type="date" required value={form.checkInDate} onChange={(e) => setForm({ ...form, checkInDate: e.target.value })} /></div>
              <div className="form-group"><label>Длительность</label><input required value={form.duration} onChange={(e) => setForm({ ...form, duration: e.target.value })} /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Цена</label><input type="number" min={1} required value={form.price} onChange={(e) => setForm({ ...form, price: +e.target.value })} /></div>
              <div className="form-group"><label>Статус</label>
                <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                  <option value="pending">pending</option><option value="confirmed">confirmed</option>
                  <option value="cancelled">cancelled</option><option value="completed">completed</option>
                </select>
              </div>
            </div>
            <AsyncMultiSelect label="Номера (M:N)" selected={form.roomIds ?? []} onChange={(ids) => setForm({ ...form, roomIds: ids })}
              loadOptions={async () => { const p = await roomApi.list(); return p.content.map((r) => ({ id: r.id, label: `${r.type} — этаж ${r.floor} (${r.price} BYN)` })); }} />
            <AsyncMultiSelect label="Платежи (M:N)" selected={form.paymentIds ?? []} onChange={(ids) => setForm({ ...form, paymentIds: ids })}
              loadOptions={async () => { const p = await paymentApi.list(); return p.content.map((pay) => ({ id: pay.id, label: `#${pay.id} — ${pay.amount} BYN (${pay.status})` })); }} />
            <div className="btn-row">
              <button type="submit" className="btn btn-primary">Сохранить</button>
              <button type="button" className="btn btn-outline" onClick={() => setShowForm(false)}>Отмена</button>
            </div>
          </form>
        </div>
      )}

      {loading && <div className="loading">Загрузка...</div>}

      <table className="data-table">
        <thead><tr><th>ID</th><th>Гость</th><th>Дата</th><th>Длительность</th><th>Номера</th><th>Платежи</th><th>Цена</th><th>Статус</th><th></th></tr></thead>
        <tbody>
          {items.map((b) => (
            <tr key={b.id}>
              <td>{b.id}</td>
              <td>{b.guest?.fullName ?? '—'}</td>
              <td>{b.checkInDate}</td>
              <td>{b.duration}</td>
              <td>{b.rooms?.map((r) => `#${r.id}`).join(', ') || '—'}</td>
              <td>{b.payments?.map((p) => `#${p.id}`).join(', ') || '—'}</td>
              <td>{b.price}</td>
              <td><span className="badge">{b.status}</span></td>
              <td className="actions">
                <button type="button" className="btn btn-sm btn-outline" onClick={() => openEdit(b)}>Изменить</button>
                <button type="button" className="btn btn-sm btn-danger" onClick={async () => { if (confirm('Удалить?')) { await bookingApi.delete(b.id); refetch(); } }}>Удалить</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <Pagination pageInfo={pageInfo} onPageChange={setPage} />
    </div>
  );
}
