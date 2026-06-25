import { useCallback, useEffect, useState } from 'react';

import { employeeApi, roomApi } from '../../api/services';

import type { Room, RoomRequest } from '../../api/types';

import { ApiError } from '../../api/client';

import { ErrorAlert } from '../../components/ui/Alert';

import { AsyncMultiSelect } from '../../components/ui/MultiSelect';

import { Pagination } from '../../components/ui/Pagination';

import { useAdminCrud } from '../../hooks/useAdminCrud';



const empty: RoomRequest = { floor: 1, type: 'standard', status: 'available', price: 100, employeeIds: [] };



export function RoomAdminPage() {

  const [form, setForm] = useState<RoomRequest>(empty);

  const crud = useAdminCrud<Room, RoomRequest>({

    queryKey: 'admin-rooms',

    listFn: roomApi.list,

    createFn: roomApi.create,

    updateFn: roomApi.update,

    deleteFn: roomApi.delete,

  });



  useEffect(() => {

    if (crud.showForm && !crud.editId) setForm(empty);

  }, [crud.showForm, crud.editId]);



  const openEdit = useCallback((r: Room) => {

    setForm({ floor: r.floor, type: r.type, status: r.status, price: r.price, employeeIds: [] });

    crud.openEdit(r);

  }, [crud]);



  const save = async (e: React.FormEvent) => {

    e.preventDefault();

    try {

      await crud.save(form);

    } catch (err) {

      if (err instanceof ApiError) crud.setError(err.message);

    }

  };



  return (

    <div>

      <div className="admin-header-row">

        <h1 className="admin-title">Номера</h1>

        <button type="button" className="btn btn-primary" onClick={crud.openCreate}>Добавить</button>

      </div>

      {crud.error && <ErrorAlert message={crud.error} />}



      {crud.showForm && (

        <div className="modal-overlay" onClick={crud.closeForm}>

          <form className="modal card" onClick={(e) => e.stopPropagation()} onSubmit={save}>

            <h2>{crud.editId ? 'Редактировать' : 'Новый'} номер</h2>

            <div className="form-row">

              <div className="form-group"><label>Этаж</label><input type="number" min={1} required value={form.floor} onChange={(e) => setForm({ ...form, floor: +e.target.value })} /></div>

              <div className="form-group"><label>Цена</label><input type="number" min={0} required value={form.price} onChange={(e) => setForm({ ...form, price: +e.target.value })} /></div>

            </div>

            <div className="form-group"><label>Тип</label>

              <select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}>

                <option value="standard">standard</option><option value="deluxe">deluxe</option>

                <option value="suite">suite</option><option value="family">family</option>

              </select>

            </div>

            <div className="form-group"><label>Статус</label>

              <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>

                <option value="available">available</option><option value="occupied">occupied</option>

                <option value="maintenance">maintenance</option>

              </select>

            </div>

            <AsyncMultiSelect label="Сотрудники (обслуживание)" selected={form.employeeIds ?? []} onChange={(ids) => setForm({ ...form, employeeIds: ids })}

              loadOptions={async () => { const p = await employeeApi.list({ size: 100 }); return p.content.map((e) => ({ id: e.id, label: e.fullName })); }} />

            <div className="btn-row">

              <button type="submit" className="btn btn-primary" disabled={crud.isSaving}>Сохранить</button>

              <button type="button" className="btn btn-outline" onClick={crud.closeForm}>Отмена</button>

            </div>

          </form>

        </div>

      )}



      <table className="data-table">

        <thead><tr><th>ID</th><th>Этаж</th><th>Тип</th><th>Статус</th><th>Цена</th><th></th></tr></thead>

        <tbody>

          {crud.items.map((r) => (

            <tr key={r.id}>

              <td>{r.id}</td><td>{r.floor}</td><td>{r.type}</td>

              <td><span className={`badge badge-${r.status}`}>{r.status}</span></td>

              <td>{r.price} BYN</td>

              <td className="actions">

                <button type="button" className="btn btn-sm btn-outline" onClick={() => openEdit(r)}>Изменить</button>

                <button type="button" className="btn btn-sm btn-danger" onClick={() => crud.remove(r.id)}>Удалить</button>

              </td>

            </tr>

          ))}

        </tbody>

      </table>



      <Pagination pageInfo={crud.pageInfo} onPageChange={crud.setPage} />

    </div>

  );

}

