import { useCallback, useEffect, useState } from 'react';
import { bookingApi, paymentApi, serviceApi } from '../../api/services';
import type { Payment, PaymentRequest } from '../../api/types';
import { ApiError } from '../../api/client';
import { ErrorAlert } from '../../components/ui/Alert';
import { AsyncMultiSelect } from '../../components/ui/MultiSelect';

const empty: PaymentRequest = {
  status: 'pending', amount: 0, paymentDate: new Date().toISOString().slice(0, 10),
  paymentMethod: 'card', bookingIds: [], serviceIds: [],
};

export function PaymentAdminPage() {
  const [items, setItems] = useState<Payment[]>([]);
  const [form, setForm] = useState<PaymentRequest>(empty);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(() => { paymentApi.list({ sort: 'paymentDate,desc' }).then((p) => setItems(p.content)); }, []);
  useEffect(() => { load(); }, [load]);

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editId) await paymentApi.update(editId, form);
      else await paymentApi.create(form);
      setShowForm(false);
      load();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Ошибка');
    }
  };

  return (
    <div>
      <div className="admin-header-row">
        <h1 className="admin-title">Платежи</h1>
        <button type="button" className="btn btn-primary" onClick={() => { setEditId(null); setForm(empty); setShowForm(true); }}>Добавить</button>
      </div>
      {error && <ErrorAlert message={error} />}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <form className="modal card" onClick={(e) => e.stopPropagation()} onSubmit={save}>
            <h2>{editId ? 'Редактировать' : 'Новый'} платёж</h2>
            <div className="form-row">
              <div className="form-group"><label>Сумма</label><input type="number" min={0} required value={form.amount} onChange={(e) => setForm({ ...form, amount: +e.target.value })} /></div>
              <div className="form-group"><label>Дата</label><input type="date" required value={form.paymentDate} onChange={(e) => setForm({ ...form, paymentDate: e.target.value })} /></div>
            </div>
            <div className="form-row">
              <div className="form-group"><label>Способ</label>
                <select value={form.paymentMethod} onChange={(e) => setForm({ ...form, paymentMethod: e.target.value })}>
                  <option value="card">card</option><option value="cash">cash</option><option value="transfer">transfer</option>
                </select>
              </div>
              <div className="form-group"><label>Статус</label>
                <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                  <option value="pending">pending</option><option value="completed">completed</option><option value="refunded">refunded</option>
                </select>
              </div>
            </div>
            <AsyncMultiSelect label="Бронирования (M:N)" selected={form.bookingIds ?? []} onChange={(ids) => setForm({ ...form, bookingIds: ids })}
              loadOptions={async () => { const p = await bookingApi.list(); return p.content.map((b) => ({ id: b.id, label: `#${b.id} — ${b.guest?.fullName ?? '?'} (${b.checkInDate})` })); }} />
            <AsyncMultiSelect label="Услуги (M:N)" selected={form.serviceIds ?? []} onChange={(ids) => setForm({ ...form, serviceIds: ids })}
              loadOptions={async () => { const p = await serviceApi.list(); return p.content.map((s) => ({ id: s.id, label: s.title })); }} />
            <div className="btn-row">
              <button type="submit" className="btn btn-primary">Сохранить</button>
              <button type="button" className="btn btn-outline" onClick={() => setShowForm(false)}>Отмена</button>
            </div>
          </form>
        </div>
      )}

      <table className="data-table">
        <thead><tr><th>ID</th><th>Сумма</th><th>Дата</th><th>Способ</th><th>Статус</th><th>Брони</th><th>Услуги</th><th></th></tr></thead>
        <tbody>
          {items.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td><td>{p.amount} BYN</td><td>{p.paymentDate}</td><td>{p.paymentMethod}</td>
              <td><span className="badge">{p.status}</span></td>
              <td>{p.bookingIds?.join(', ') || '—'}</td>
              <td>{p.serviceIds?.join(', ') || '—'}</td>
              <td className="actions">
                <button type="button" className="btn btn-sm btn-outline" onClick={() => { setEditId(p.id); setForm({ status: p.status, amount: p.amount, paymentDate: p.paymentDate, paymentMethod: p.paymentMethod, bookingIds: p.bookingIds ?? [], serviceIds: p.serviceIds ?? [] }); setShowForm(true); }}>Изменить</button>
                <button type="button" className="btn btn-sm btn-danger" onClick={async () => { if (confirm('Удалить?')) { await paymentApi.delete(p.id); load(); } }}>Удалить</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
