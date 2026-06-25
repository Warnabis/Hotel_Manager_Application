import { useCallback, useEffect, useState } from 'react';
import { employeeApi, positionApi } from '../../api/services';
import type { Position, PositionRequest } from '../../api/types';
import { ApiError } from '../../api/client';
import { ErrorAlert } from '../../components/ui/Alert';
import { AsyncMultiSelect } from '../../components/ui/MultiSelect';

const empty: PositionRequest = { title: '', salary: 0, responsibilities: '', employeeIds: [] };

export function PositionAdminPage() {
  const [items, setItems] = useState<Position[]>([]);
  const [form, setForm] = useState<PositionRequest>(empty);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(() => { positionApi.list().then((p) => setItems(p.content)); }, []);
  useEffect(() => { load(); }, [load]);

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editId) await positionApi.update(editId, form);
      else await positionApi.create(form);
      setShowForm(false);
      load();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Ошибка');
    }
  };

  return (
    <div>
      <div className="admin-header-row">
        <h1 className="admin-title">Должности</h1>
        <button type="button" className="btn btn-primary" onClick={() => { setEditId(null); setForm(empty); setShowForm(true); }}>Добавить</button>
      </div>
      {error && <ErrorAlert message={error} />}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <form className="modal card" onClick={(e) => e.stopPropagation()} onSubmit={save}>
            <h2>{editId ? 'Редактировать' : 'Новая'} должность</h2>
            <div className="form-group"><label>Название</label><input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></div>
            <div className="form-group"><label>Зарплата</label><input type="number" min={0} required value={form.salary} onChange={(e) => setForm({ ...form, salary: +e.target.value })} /></div>
            <div className="form-group"><label>Обязанности</label><textarea required rows={4} value={form.responsibilities} onChange={(e) => setForm({ ...form, responsibilities: e.target.value })} /></div>
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
        <thead><tr><th>ID</th><th>Название</th><th>Зарплата</th><th>Обязанности</th><th>Сотрудники</th><th></th></tr></thead>
        <tbody>
          {items.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td><td>{p.title}</td><td>{p.salary} BYN</td>
              <td className="truncate">{p.responsibilities}</td>
              <td>{p.employeeIds?.join(', ') || '—'}</td>
              <td className="actions">
                <button type="button" className="btn btn-sm btn-outline" onClick={() => { setEditId(p.id); setForm({ title: p.title, salary: p.salary, responsibilities: p.responsibilities, employeeIds: p.employeeIds ?? [] }); setShowForm(true); }}>Изменить</button>
                <button type="button" className="btn btn-sm btn-danger" onClick={async () => { if (confirm('Удалить?')) { await positionApi.delete(p.id); load(); } }}>Удалить</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
