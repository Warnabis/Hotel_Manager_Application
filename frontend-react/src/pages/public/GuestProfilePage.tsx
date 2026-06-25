import { useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { bookingApi, guestApi, serviceApi, authApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { ApiError } from '../../api/client';
import { ErrorAlert, SuccessAlert } from '../../components/ui/Alert';
import { useState } from 'react';
import type { Booking } from '../../api/types';

export function GuestProfilePage() {
  const { guest, setGuest, logout, refreshGuest } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [deleting, setDeleting] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [editForm, setEditForm] = useState({
    fullName: '',
    phoneNumber: '',
    email: '',
  });
  const [passwordForm, setPasswordForm] = useState({
    newPassword: '',
    confirmPassword: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isPasswordSubmitting, setIsPasswordSubmitting] = useState(false);

  const { data: bookingsPage, isLoading: bookingsLoading, refetch: refetchBookings } = useQuery({
    queryKey: ['bookings', guest?.id],
    queryFn: () => bookingApi.list({ guestId: guest!.id }),
    enabled: !!guest,
  });

  const { data: services = [], isLoading: servicesLoading, refetch: refetchServices } = useQuery({
    queryKey: ['profile-services', guest?.id],
    queryFn: async () => {
      const g = await guestApi.get(guest!.id);
      if (!g.serviceIds?.length) return [];
      const all = await serviceApi.list({ size: 100 });
      return all.content.filter((s) => g.serviceIds!.includes(s.id));
    },
    enabled: !!guest,
  });

  const handleDeleteAccount = async () => {
    if (!confirm('Удалить аккаунт без возможности восстановления?')) return;
    setDeleting(true);
    setError('');
    try {
      await authApi.deleteAccount();
      logout();
      navigate('/');
    } catch (e) {
      setError(e instanceof ApiError ? e.message : 'Ошибка удаления');
    } finally {
      setDeleting(false);
    }
  };

  const handleRefresh = () => refreshGuest().catch(() => {});

  const startEditing = () => {
    if (guest) {
      setEditForm({
        fullName: guest.fullName,
        phoneNumber: guest.phoneNumber,
        email: guest.email,
      });
      setIsEditing(true);
    }
  };

  const cancelEditing = () => {
    setIsEditing(false);
    setError('');
  };

  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!guest) return;

    setIsSubmitting(true);
    setError('');

    try {
      const updated = await guestApi.update(guest.id, {
        fullName: editForm.fullName.trim(),
        phoneNumber: editForm.phoneNumber.trim(),
        email: editForm.email.trim().toLowerCase(),
        status: guest.status,
        serviceIds: guest.serviceIds || [],
      });

      setGuest(updated);
      setIsEditing(false);
      setSuccess('Данные успешно обновлены');
      setTimeout(() => setSuccess(''), 3000);
      queryClient.invalidateQueries({ queryKey: ['profile-services'] });
    } catch (e) {
      setError(e instanceof ApiError ? e.message : 'Ошибка обновления данных');
    } finally {
      setIsSubmitting(false);
    }
  };

  const startPasswordChange = () => {
    setPasswordForm({
      newPassword: '',
      confirmPassword: '',
    });
    setIsChangingPassword(true);
    setError('');
  };

  const cancelPasswordChange = () => {
    setIsChangingPassword(false);
    setError('');
  };

  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!guest) return;

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setError('Пароли не совпадают');
      return;
    }

    if (passwordForm.newPassword.length < 6) {
      setError('Пароль должен содержать минимум 6 символов');
      return;
    }

    setIsPasswordSubmitting(true);
    setError('');

    try {
      const response = await authApi.changePassword({
        newPassword: passwordForm.newPassword,
      });

      // Проверяем, что ответ успешный
      if (response) {
        setIsChangingPassword(false);
        setPasswordForm({
          newPassword: '',
          confirmPassword: '',
        });
        setSuccess('Пароль успешно изменён');
        setTimeout(() => setSuccess(''), 3000);
      }
    } catch (e) {
      // Если это ApiError, показываем сообщение
      if (e instanceof ApiError) {
        // Если статус 200 или 204, значит всё ок
        if (e.status === 200 || e.status === 204 || e.status === 0) {
          setIsChangingPassword(false);
          setPasswordForm({
            newPassword: '',
            confirmPassword: '',
          });
          setSuccess('Пароль успешно изменён');
          setTimeout(() => setSuccess(''), 3000);
          return;
        }
        setError(e.message || 'Ошибка изменения пароля');
      } else {
        // Если ошибка с сообщением
        const errorMessage = e instanceof Error ? e.message : 'Ошибка изменения пароля';
        setError(errorMessage);
      }
    } finally {
      setIsPasswordSubmitting(false);
    }
  };

  const removeService = async (serviceId: number) => {
    if (!guest) return;
    const newIds = (guest.serviceIds ?? []).filter((id) => id !== serviceId);
    try {
      const updated = await guestApi.update(guest.id, {
        fullName: guest.fullName,
        phoneNumber: guest.phoneNumber,
        email: guest.email,
        status: guest.status,
        serviceIds: newIds,
      });
      setGuest(updated);
      await refetchServices();
      queryClient.invalidateQueries({ queryKey: ['profile-services'] });
      setSuccess('Услуга удалена');
      setTimeout(() => setSuccess(''), 3000);
    } catch (e) {
      setError(e instanceof ApiError ? e.message : 'Ошибка удаления услуги');
    }
  };

  const cancelBooking = async (booking: Booking) => {
    if (!confirm('Отменить бронирование?')) return;
    try {
      await bookingApi.delete(booking.id);

      await refetchBookings();
      queryClient.invalidateQueries({ queryKey: ['bookings'] });

      queryClient.setQueryData(['bookings', guest?.id], (oldData: any) => {
        if (!oldData) return oldData;
        return {
          ...oldData,
          content: oldData.content.filter((b: Booking) => b.id !== booking.id)
        };
      });

      setSuccess(`Бронирование #${booking.id} отменено`);
      setTimeout(() => setSuccess(''), 3000);
    } catch (e) {
      let message = 'Ошибка отмены бронирования';
      if (e instanceof ApiError) {
        try {
          message = decodeURIComponent(e.message);
        } catch {
          message = e.message;
        }
      }
      setError(message);
    }
  };

  if (!guest) return <Navigate to="/login?redirect=/profile" replace />;

  const bookings = bookingsPage?.content ?? [];

  return (
      <div className="container page">
        <div className="page-header">
          <h1>Мой профиль</h1>
          <p>Управление бронированиями и услугами</p>
        </div>

        {error && <ErrorAlert message={error} onClose={() => setError('')} />}
        {success && <SuccessAlert message={success} />}

        <div className="profile-grid">
          <section className="card profile-card">
            <div className="section-header-row">
              <h2>Личные данные</h2>
              {!isEditing && !isChangingPassword && (
                  <button type="button" className="btn btn-sm btn-outline" onClick={startEditing}>
                    Редактировать
                  </button>
              )}
            </div>

            {isEditing ? (
                <form onSubmit={handleEditSubmit}>
                  <div className="form-group">
                    <label>ФИО *</label>
                    <input
                        type="text"
                        value={editForm.fullName}
                        onChange={(e) => setEditForm({ ...editForm, fullName: e.target.value })}
                        required
                    />
                  </div>
                  <div className="form-group">
                    <label>Телефон *</label>
                    <input
                        type="text"
                        value={editForm.phoneNumber}
                        onChange={(e) => setEditForm({ ...editForm, phoneNumber: e.target.value })}
                        required
                    />
                  </div>
                  <div className="form-group">
                    <label>Email *</label>
                    <input
                        type="email"
                        value={editForm.email}
                        onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                        required
                    />
                  </div>
                  <div className="btn-row" style={{ marginTop: '1rem' }}>
                    <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                      {isSubmitting ? 'Сохранение...' : 'Сохранить'}
                    </button>
                    <button type="button" className="btn btn-outline" onClick={cancelEditing}>
                      Отмена
                    </button>
                  </div>
                </form>
            ) : isChangingPassword ? (
                <form onSubmit={handlePasswordSubmit}>
                  <div className="form-group">
                    <label>Новый пароль *</label>
                    <input
                        type="password"
                        value={passwordForm.newPassword}
                        onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                        required
                        minLength={6}
                    />
                  </div>
                  <div className="form-group">
                    <label>Подтверждение пароля *</label>
                    <input
                        type="password"
                        value={passwordForm.confirmPassword}
                        onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                        required
                    />
                  </div>
                  <div className="btn-row" style={{ marginTop: '1rem' }}>
                    <button type="submit" className="btn btn-primary" disabled={isPasswordSubmitting}>
                      {isPasswordSubmitting ? 'Сохранение...' : 'Сменить пароль'}
                    </button>
                    <button type="button" className="btn btn-outline" onClick={cancelPasswordChange}>
                      Отмена
                    </button>
                  </div>
                </form>
            ) : (
                <>
                  <dl className="detail-list">
                    <dt>ФИО</dt><dd>{guest.fullName}</dd>
                    <dt>Email</dt><dd>{guest.email}</dd>
                    <dt>Телефон</dt><dd>{guest.phoneNumber}</dd>
                    <dt>Статус</dt><dd><span className="badge">{guest.status}</span></dd>
                  </dl>
                  <div className="btn-row">
                    <Link to="/booking" className="btn btn-accent">Новое бронирование</Link>
                    <Link to="/services" className="btn btn-outline">Каталог услуг</Link>
                  </div>
                  <div style={{ marginTop: '1rem' }}>
                    <button type="button" className="btn btn-outline btn-sm" onClick={startPasswordChange}>
                      Сменить пароль
                    </button>
                  </div>
                </>
            )}

            <div className="profile-danger-zone" style={{ marginTop: '1.5rem' }}>
              <button type="button" className="btn btn-danger btn-block" onClick={handleDeleteAccount} disabled={deleting}>
                {deleting ? 'Удаление...' : 'Удалить аккаунт'}
              </button>
            </div>
          </section>

          <section className="card">
            <div className="section-header-row">
              <h2>Мои бронирования</h2>
            </div>
            {bookingsLoading && <div className="loading">Загрузка...</div>}
            {!bookingsLoading && bookings.length === 0 && (
                <p className="text-muted">У вас пока нет бронирований</p>
            )}
            <div className="booking-list">
              {bookings.map((b) => (
                  <div key={b.id} className="booking-item">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div>
                        <strong>Бронь #{b.id}</strong>
                        <span className={`badge badge-${b.status}`}>{b.status}</span>
                      </div>
                      <button
                          type="button"
                          className="btn btn-sm btn-danger"
                          onClick={() => cancelBooking(b)}
                      >
                        Отменить
                      </button>
                    </div>
                    <p>Заезд: {b.checkInDate} · {b.duration}</p>
                    <p>Номера: {b.rooms?.map((r) => r.type).join(', ') || '—'}</p>
                    <p>Цена: {b.price.toLocaleString('ru-RU')} BYN</p>
                    {b.payments?.length ? (
                        <p>Платежи: {b.payments.map((p) => `#${p.id} (${p.amount} BYN)`).join(', ')}</p>
                    ) : null}
                  </div>
              ))}
            </div>
          </section>

          <section className="card">
            <div className="section-header-row">
              <h2>Мои услуги</h2>
              <button type="button" className="btn btn-sm btn-outline" onClick={handleRefresh}>Обновить</button>
            </div>
            {servicesLoading && <div className="loading">Загрузка...</div>}
            {!servicesLoading && services.length === 0 ? (
                <p className="text-muted">Нет заказанных услуг. <Link to="/services">Посмотреть каталог</Link></p>
            ) : (
                <ul className="service-list profile-service-list">
                  {services.map((s) => (
                      <li key={s.id}>
                        <strong>{s.title}</strong>
                        <span>{s.price.toLocaleString('ru-RU')} BYN · {s.duration}</span>
                        <button
                            type="button"
                            className="btn btn-sm btn-danger"
                            onClick={() => removeService(s.id)}
                        >
                          Отменить
                        </button>
                      </li>
                  ))}
                </ul>
            )}
          </section>
        </div>
      </div>
  );
}