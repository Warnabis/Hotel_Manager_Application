import { useCallback, useEffect, useState } from 'react';
import { employeeApi, positionApi, roomApi, serviceApi } from '../../api/services';
import type { Employee, EmployeeRequest } from '../../api/types';
import { ApiError } from '../../api/client';
import { ErrorAlert } from '../../components/ui/Alert';
import { AsyncMultiSelect } from '../../components/ui/MultiSelect';

const empty: EmployeeRequest = { fullName: '', experience: '', schedule: '', phoneNumber: '', positionIds: [], roomIds: [], serviceIds: [] };

export function EmployeeAdminPage() {
  const [items, setItems] = useState<Employee[]>([]);
  const [form, setForm] = useState<EmployeeRequest>(empty);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(() => { employeeApi.list().then((p) => setItems(p.content)); }, []);
  useEffect(() => { load(); }, [load]);

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editId) await employeeApi.update(editId, form);
      else await employeeApi.create(form);
      setShowForm(false);
      load();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Ошибка');
    }
  };

  return (
    <div>
      <div className="admin-header-row">
        <h1 className="admin-title">Сотрудники</h1>
        <button type="button" className="btn btn-primary" onClick={() => { setEditId(null); setForm(empty); setShowForm(true); }}>Добавить</button>
      </div>
      {error && <ErrorAlert message={error} />}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <form className="modal card modal-lg" onClick={(e) => e.stopPropagation()} onSubmit={save}>
            <h2>{editId ? 'Редактировать' : 'Новый'} сотрудник</h2>
            <div className="form-group"><label>ФИО</label><input required value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></div>
            <div className="form-group"><label>Телефон</label><input required value={form.phoneNumber} onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })} /></div>
            <div className="form-group"><label>Опыт</label><input required value={form.experience} onChange={(e) => setForm({ ...form, experience: e.target.value })} /></div>
            <div className="form-group"><label>График</label><input required value={form.schedule} onChange={(e) => setForm({ ...form, schedule: e.target.value })} /></div>
            <AsyncMultiSelect label="Должности (M:N)" selected={form.positionIds ?? []} onChange={(ids) => setForm({ ...form, positionIds: ids })}
              loadOptions={async () => { const p = await positionApi.list(); return p.content.map((pos) => ({ id: pos.id, label: pos.title })); }} />
            <AsyncMultiSelect label="Номера (M:N)" selected={form.roomIds ?? []} onChange={(ids) => setForm({ ...form, roomIds: ids })}
              loadOptions={async () => { const p = await roomApi.list(); return p.content.map((r) => ({ id: r.id, label: `${r.type} #${r.id}` })); }} />
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
        <thead><tr><th>ID</th><th>ФИО</th><th>Телефон</th><th>График</th><th>Должности</th><th>Номера</th><th>Услуги</th><th></th></tr></thead>
        <tbody>
          {items.map((e) => (
            <tr key={e.id}>
              <td>{e.id}</td><td>{e.fullName}</td><td>{e.phoneNumber}</td><td>{e.schedule}</td>
              <td>{e.positionIds?.join(', ') || '—'}</td>
              <td>{e.roomIds?.join(', ') || '—'}</td>
              <td>{e.serviceIds?.join(', ') || '—'}</td>
              <td className="actions">
                <button type="button" className="btn btn-sm btn-outline" onClick={() => { setEditId(e.id); setForm({ fullName: e.fullName, experience: e.experience, schedule: e.schedule, phoneNumber: e.phoneNumber, positionIds: e.positionIds ?? [], roomIds: e.roomIds ?? [], serviceIds: e.serviceIds ?? [] }); setShowForm(true); }}>Изменить</button>
                <button type="button" className="btn btn-sm btn-danger" onClick={async () => { if (confirm('Удалить?')) { await employeeApi.delete(e.id); load(); } }}>Удалить</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
