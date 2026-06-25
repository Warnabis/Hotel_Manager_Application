import { useQuery, useQueryClient } from '@tanstack/react-query';

import { serviceApi, guestApi } from '../../api/services';

import type { Service } from '../../api/types';

import { useAuth } from '../../context/AuthContext';

import { ApiError } from '../../api/client';

import { ErrorAlert, SuccessAlert } from '../../components/ui/Alert';

import { useState } from 'react';



export function ServicesPage() {

  const { guest, setGuest } = useAuth();

  const queryClient = useQueryClient();

  const [error, setError] = useState('');

  const [success, setSuccess] = useState('');

  const [orderingId, setOrderingId] = useState<number | null>(null);



  const { data, isLoading } = useQuery({

    queryKey: ['services'],

    queryFn: () => serviceApi.list({ size: 100 }),

  });



  const services = data?.content ?? [];



  const orderService = async (service: Service) => {

    if (!guest) {

      setError('Войдите, чтобы заказать услугу');

      return;

    }

    setError('');

    setSuccess('');

    setOrderingId(service.id);

    try {

      const currentIds = guest.serviceIds ?? [];

      if (currentIds.includes(service.id)) {

        setSuccess(`Услуга «${service.title}» уже добавлена к вашему профилю`);

        return;

      }

      const updated = await guestApi.update(guest.id, {

        fullName: guest.fullName,

        phoneNumber: guest.phoneNumber,

        email: guest.email,

        status: guest.status,

        serviceIds: [...currentIds, service.id],

      });

      setGuest(updated);

      queryClient.invalidateQueries({ queryKey: ['profile-services'] });

      setSuccess(`Услуга «${service.title}» добавлена к вашему профилю`);

    } catch (e) {

      setError(e instanceof ApiError ? e.message : 'Ошибка');

    } finally {

      setOrderingId(null);

    }

  };



  return (

    <div className="container page">

      <div className="page-header">

        <h1>Услуги отеля</h1>

        <p>SPA, питание, трансфер и дополнительные сервисы</p>

      </div>



      {error && <ErrorAlert message={error} onClose={() => setError('')} />}

      {success && <SuccessAlert message={success} />}



      {isLoading && <div className="loading">Загрузка...</div>}



      <div className="services-grid">

        {services.map((s) => (

          <article key={s.id} className="service-card card">

            <div className="service-badge">{s.duration}</div>

            <h3>{s.title}</h3>

            <p>{s.description}</p>

            <div className="service-meta">

              <span>{s.duration}</span>

              <strong>{s.price.toLocaleString('ru-RU')} BYN</strong>

            </div>

            <button
              type="button"
              className="btn btn-primary btn-block"
              onClick={() => orderService(s)}
              disabled={orderingId === s.id}
            >

              {!guest
                ? 'Войти для заказа'
                : orderingId === s.id
                  ? 'Добавление...'
                  : guest.serviceIds?.includes(s.id)
                    ? 'Добавлено'
                    : 'Заказать'}

            </button>

          </article>

        ))}

      </div>

    </div>

  );

}

