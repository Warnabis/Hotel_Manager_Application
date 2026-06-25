import { useEffect, useState } from 'react';
import { bookingApi, guestApi, paymentApi, roomApi, serviceApi, employeeApi } from '../../api/services';
import { ApiError } from '../../api/client';
import { ErrorAlert } from '../../components/ui/Alert';

export function DashboardPage() {
  const [stats, setStats] = useState({ guests: 0, rooms: 0, bookings: 0, payments: 0, services: 0, employees: 0 });
  const [recentBookings, setRecentBookings] = useState<Awaited<ReturnType<typeof bookingApi.list>>['content']>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([
      guestApi.list({ size: 1 }),
      roomApi.list({ size: 1 }),
      bookingApi.list({ size: 5, sort: 'checkInDate,desc' }),
      paymentApi.list({ size: 1 }),
      serviceApi.list({ size: 1 }),
      employeeApi.list({ size: 1 }),
    ])
      .then(([g, r, b, p, s, e]) => {
        setStats({
          guests: g.totalElements,
          rooms: r.totalElements,
          bookings: b.totalElements,
          payments: p.totalElements,
          services: s.totalElements,
          employees: e.totalElements,
        });
        setRecentBookings(b.content);
      })
      .catch((err: ApiError) => setError(err.message));
  }, []);

  return (
    <div>
      <h1 className="admin-title">Дашборд</h1>
      {error && <ErrorAlert message={error} />}

      <div className="stats-grid">
        {[
          { label: 'Гости', value: stats.guests },
          { label: 'Номера', value: stats.rooms },
          { label: 'Бронирования', value: stats.bookings },
          { label: 'Платежи', value: stats.payments },
          { label: 'Услуги', value: stats.services },
          { label: 'Сотрудники', value: stats.employees },
        ].map((s) => (
          <div key={s.label} className="stat-card">
            <div className="stat-icon">{s.label.charAt(0)}</div>
            <div>
              <div className="stat-value">{s.value}</div>
              <div className="stat-label">{s.label}</div>
            </div>
          </div>
        ))}
      </div>

      <section className="card admin-section">
        <h2>Последние бронирования</h2>
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Гость</th>
              <th>Дата</th>
              <th>Номера</th>
              <th>Цена</th>
              <th>Статус</th>
            </tr>
          </thead>
          <tbody>
            {recentBookings.map((b) => (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>{b.guest?.fullName ?? '—'}</td>
                <td>{b.checkInDate}</td>
                <td>{b.rooms?.map((r) => r.type).join(', ') || '—'}</td>
                <td>{b.price} BYN</td>
                <td><span className="badge">{b.status}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}
