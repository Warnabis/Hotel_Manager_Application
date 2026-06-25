import { useEffect, useState } from 'react';

import { useForm } from 'react-hook-form';

import { zodResolver } from '@hookform/resolvers/zod';

import { guestApi, serviceApi } from '../../api/services';

import type { Guest, GuestRequest } from '../../api/types';

import { ErrorAlert } from '../../components/ui/Alert';

import { AsyncMultiSelect } from '../../components/ui/MultiSelect';

import { Pagination } from '../../components/ui/Pagination';

import { useAdminCrud } from '../../hooks/useAdminCrud';

import { guestSchema, type GuestFormValues } from '../../lib/schemas';



const emptyGuest: GuestFormValues = {

  fullName: '',

  phoneNumber: '',

  email: '',

  status: 'active',

  password: '',

  serviceIds: [],

};



export function GuestAdminPage() {

  const [filter, setFilter] = useState('');

  const crud = useAdminCrud<Guest, GuestRequest>({

    queryKey: 'admin-guests',

    listFn: guestApi.list,

    createFn: guestApi.create,

    updateFn: guestApi.update,

    deleteFn: guestApi.delete,

    filterParams: { fullName: filter || undefined },

  });



  const { register, handleSubmit, reset, setValue, watch, formState: { errors } } = useForm<GuestFormValues>({

    resolver: zodResolver(guestSchema),

    defaultValues: emptyGuest,

  });



  const serviceIds = watch('serviceIds') ?? [];



  useEffect(() => {

    if (crud.showForm && !crud.editId) reset(emptyGuest);

  }, [crud.showForm, crud.editId, reset]);



  const onEdit = (g: Guest) => {

    reset({

      fullName: g.fullName,

      phoneNumber: g.phoneNumber,

      email: g.email,

      status: g.status as GuestFormValues['status'],

      password: '',

      serviceIds: g.serviceIds ?? [],

    });

    crud.openEdit(g);

  };



  const onSubmit = async (data: GuestFormValues) => {

    const payload: GuestRequest = {

      fullName: data.fullName,

      phoneNumber: data.phoneNumber,

      email: data.email,

      status: data.status,

      serviceIds: data.serviceIds ?? [],

    };

    if (data.password) payload.password = data.password;

    await crud.save(payload);

    reset(emptyGuest);

  };



  return (

    <div>

      <div className="admin-header-row">

        <h1 className="admin-title">Гости</h1>

        <button type="button" className="btn btn-primary" onClick={crud.openCreate}>Добавить</button>

      </div>



      <input

        className="filter-input"

        placeholder="Поиск по имени..."

        value={filter}

        onChange={(e) => { setFilter(e.target.value); crud.setPage(0); }}

      />



      {crud.error && <ErrorAlert message={crud.error} validationErrors={crud.validationErrors} />}



      {crud.showForm && (

        <div className="modal-overlay" onClick={crud.closeForm}>

          <form className="modal card" onClick={(e) => e.stopPropagation()} onSubmit={handleSubmit(onSubmit)}>

            <h2>{crud.editId ? 'Редактировать' : 'Новый'} гость</h2>

            <div className="form-group">

              <label>ФИО</label>

              <input {...register('fullName')} />

              {errors.fullName && <span className="field-error">{errors.fullName.message}</span>}

            </div>

            <div className="form-group">

              <label>Телефон</label>

              <input {...register('phoneNumber')} />

              {errors.phoneNumber && <span className="field-error">{errors.phoneNumber.message}</span>}

            </div>

            <div className="form-group">

              <label>Email</label>

              <input type="email" {...register('email')} />

              {errors.email && <span className="field-error">{errors.email.message}</span>}

            </div>

            <div className="form-group">

              <label>Пароль {crud.editId ? '(оставьте пустым, чтобы не менять)' : ''}</label>

              <input type="password" {...register('password')} />

              {errors.password && <span className="field-error">{errors.password.message}</span>}

            </div>

            <div className="form-group">

              <label>Статус</label>

              <select {...register('status')}>

                <option value="active">active</option>

                <option value="inactive">inactive</option>

                <option value="vip">vip</option>

              </select>

            </div>

            <AsyncMultiSelect

              label="Услуги"

              selected={serviceIds}

              onChange={(ids) => setValue('serviceIds', ids)}

              loadOptions={async () => {

                const p = await serviceApi.list({ size: 100 });

                return p.content.map((s) => ({ id: s.id, label: s.title }));

              }}

            />

            <div className="btn-row">

              <button type="submit" className="btn btn-primary" disabled={crud.isSaving}>

                {crud.isSaving ? 'Сохранение...' : 'Сохранить'}

              </button>

              <button type="button" className="btn btn-outline" onClick={crud.closeForm}>Отмена</button>

            </div>

          </form>

        </div>

      )}



      {crud.loading && <div className="loading">Загрузка...</div>}



      <table className="data-table">

        <thead><tr><th>ID</th><th>ФИО</th><th>Email</th><th>Телефон</th><th>Статус</th><th>Услуги</th><th></th></tr></thead>

        <tbody>

          {crud.items.map((g) => (

            <tr key={g.id}>

              <td>{g.id}</td><td>{g.fullName}</td><td>{g.email}</td><td>{g.phoneNumber}</td>

              <td><span className="badge">{g.status}</span></td>

              <td>{g.serviceIds?.join(', ') || '—'}</td>

              <td className="actions">

                <button type="button" className="btn btn-sm btn-outline" onClick={() => onEdit(g)}>Изменить</button>

                <button type="button" className="btn btn-sm btn-danger" onClick={() => crud.remove(g.id)}>Удалить</button>

              </td>

            </tr>

          ))}

        </tbody>

      </table>



      <Pagination pageInfo={crud.pageInfo} onPageChange={crud.setPage} />

    </div>

  );

}

