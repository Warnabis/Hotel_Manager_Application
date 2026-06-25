import { useCallback, useEffect, useState } from 'react';
import { employeeApi, guestApi, serviceApi } from '../../api/services';
import type { Service, ServiceRequest } from '../../api/types';
import { ApiError } from '../../api/client';
import { ErrorAlert } from '../../components/ui/Alert';
import { AsyncMultiSelect } from '../../components/ui/MultiSelect';

const empty: ServiceRequest = { title: '', description: '', price: 0, duration: '1 час', guestIds: [], employeeIds: [] };

export function ServiceAdminPage() {
  const [items, setItems] = useState<Service[]>([]);
  const [form, setForm] = useState<ServiceRequest>(empty);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(() => { serviceApi.list().then((p) => setItems(p.content)); }, []);
  useEffect(() => { load(); }, [load]);

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editId) await serviceApi.update(editId, form);
      else await serviceApi.create(form);
      setShowForm(false);
      load();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Ошибка');
    }
  };

  return (
    <div>
      <div className="admin-header-row">
        <h1 className="admin-title">Услуги</h1>
        <button type="button" className="btn btn-primary" onClick={() => { setEditId(null); setForm(empty); setShowForm(true); }}>Добавить</button>
      </div>
      {error && <ErrorAlert message={error} />}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <form className="modal card" onClick={(e) => e.stopPropagation()} onSubmit={save}>
            <h2>{editId ? 'Редактировать' : 'Новая'} услуга</h2>
            <div className="form-group"><label>Название</label><input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></div>
            <div className="form-group"><label>Описание</label><textarea required rows={3} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></div>
            <div className="form-row">
              <div className="form-group"><label>Цена</label><input type="number" min={0} required value={form.price} onChange={(e) => setForm({ ...form, price: +e.target.value })} /></div>
              <div className="form-group"><label>Длительность</label><input required value={form.duration} onChange={(e) => setForm({ ...form, duration: e.target.value })} /></div>
            </div>
            <AsyncMultiSelect label="Гости (M:N)" selected={form.guestIds ?? []} onChange={(ids) => setForm({ ...form, guestIds: ids })}
              loadOptions={async () => { const p = await guestApi.list(); return p.content.map((g) => ({ id: g.id, label: g.fullName })); }} />
            <AsyncMultiSelect label="Сотрудники (M:N)" selected={form.employeeIds ?? []} onChange={(ids) => setForm({ ...form, employeeIds: ids })}
              loadOptions={async () => { const p = await employeeApi.list(); return p.content.map((e) => ({ id: e.id, label: e.fullName })); }} />
            <div className="btn-row">
              <button type="submit" className="btn btn-primary">Сохранить</button>
              <button type="button" className="btn btn-outline" onClick={() => setShowForm(false)}>Отмена</button>
            </div>
          </form>
        </div>
      )}

      <table className="data-table">
        <thead><tr><th>ID</th><th>Название</th><th>Цена</th><th>Длительность</th><th>Гости</th><th>Сотрудники</th><th></th></tr></thead>
        <tbody>
          {items.map((s) => (
            <tr key={s.id}>
              <td>{s.id}</td><td>{s.title}</td><td>{s.price}</td><td>{s.duration}</td>
              <td>{s.guestIds?.join(', ') || '—'}</td>
              <td>{s.employeeIds?.join(', ') || '—'}</td>
              <td className="actions">
                <button type="button" className="btn btn-sm btn-outline" onClick={() => { setEditId(s.id); setForm({ title: s.title, description: s.description, price: s.price, duration: s.duration, guestIds: s.guestIds ?? [], employeeIds: s.employeeIds ?? [] }); setShowForm(true); }}>Изменить</button>
                <button type="button" className="btn btn-sm btn-danger" onClick={async () => { if (confirm('Удалить?')) { await serviceApi.delete(s.id); load(); } }}>Удалить</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
