import { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { bookingApi, paymentApi, roomApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { ApiError } from '../../api/client';
import { ErrorAlert, SuccessAlert } from '../../components/ui/Alert';
import { durationToNights, ROOM_TYPE_LABELS } from '../../lib/schemas';

export function BookingPage() {
  const { guest } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [params] = useSearchParams();
  const preRoomId = params.get('roomId');

  const { data: roomsPage } = useQuery({
    queryKey: ['rooms-all'],
    queryFn: () => roomApi.list({ size: 100 }),
  });
  const allRooms = roomsPage?.content ?? [];
  const availableRooms = allRooms.filter((r) => r.status === 'available');

  const [selectedRooms, setSelectedRooms] = useState<number[]>(
      preRoomId ? [Number(preRoomId)] : []
  );
  const [checkInDate, setCheckInDate] = useState(params.get('checkIn') ?? '');
  const [duration, setDuration] = useState(params.get('duration') ?? '1 ночь');
  const [paymentMethod, setPaymentMethod] = useState('card');
  const [error, setError] = useState('');
  const [validationErrors, setValidationErrors] = useState<Record<string, string>>();
  const [success, setSuccess] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (preRoomId) {
      const id = Number(preRoomId);
      setSelectedRooms((prev) =>
          prev.includes(id) ? prev : [...prev, id]
      );
    }
  }, [preRoomId]);

  const nights = durationToNights(duration);
  const selectedRoomObjects = allRooms.filter((r) => selectedRooms.includes(r.id));
  const nightlyTotal = selectedRoomObjects.reduce((sum, r) => sum + r.price, 0);
  const totalPrice = nightlyTotal * nights;

  const roomsLink = `/rooms?checkIn=${checkInDate}&duration=${encodeURIComponent(duration)}`;

  const toggleRoom = (id: number) => {
    setSelectedRooms((prev) =>
        prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const clearSelection = () => setSelectedRooms([]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!guest) {
      navigate('/login?redirect=/booking');
      return;
    }
    if (!selectedRooms.length) {
      setError('Выберите хотя бы один номер');
      return;
    }
    if (!checkInDate) {
      setError('Укажите дату заезда');
      return;
    }
    setSubmitting(true);
    setError('');
    setValidationErrors(undefined);
    try {
      const booking = await bookingApi.create({
        price: totalPrice,
        status: 'confirmed',
        checkInDate,
        duration,
        guestId: guest.id,
        roomIds: selectedRooms,
        paymentIds: [],
      });

      if (totalPrice > 0) {
        try {
          await paymentApi.create({
            status: 'completed',
            amount: totalPrice,
            paymentDate: checkInDate,
            paymentMethod,
            bookingIds: [booking.id],
            serviceIds: [],
          });
        } catch (paymentErr) {
          console.warn('Payment creation failed:', paymentErr);
        }
      }

      setSuccess('Бронирование успешно создано!');
      queryClient.invalidateQueries({ queryKey: ['bookings'] });
      setTimeout(() => navigate('/profile'), 2000);
    } catch (err) {
      let message = 'Ошибка при бронировании';
      if (err instanceof ApiError) {
        message = err.message;
        setValidationErrors(err.validationErrors);
        if (message && (message.toLowerCase().includes('room') || message.toLowerCase().includes('constraint'))) {
          message = 'Сервер не позволяет забронировать несколько комнат в одном заказе. Оформите отдельные брони.';
        }
      }
      setError(message);
    } finally {
      setSubmitting(false);
    }
  };

  if (!guest) {
    return (
        <div className="container page">
          <div className="auth-prompt">
            <h2>Для бронирования необходимо войти</h2>
            <p>Войдите в аккаунт или зарегистрируйтесь, чтобы оформить бронь</p>
            <div className="btn-row">
              <Link to="/login?redirect=/booking" className="btn btn-primary">Войти</Link>
              <Link to="/register" className="btn btn-outline">Регистрация</Link>
            </div>
          </div>
        </div>
    );
  }

  return (
      <div className="container page">
        <div className="page-header">
          <h1>Оформление бронирования</h1>
          <p>Гость: <strong>{guest.fullName}</strong> ({guest.email})</p>
        </div>

        {error && <ErrorAlert message={error} validationErrors={validationErrors} onClose={() => setError('')} />}
        {success && <SuccessAlert message={success} />}

        <form className="booking-form card" onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Дата заезда *</label>
              <input type="date" required value={checkInDate} onChange={(e) => setCheckInDate(e.target.value)} />
            </div>
            <div className="form-group">
              <label>Длительность *</label>
              <select value={duration} onChange={(e) => setDuration(e.target.value)}>
                <option>1 ночь</option>
                <option>2 ночи</option>
                <option>3 ночи</option>
                <option>1 неделя</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <div className="form-label-row">
              <label>Номера *</label>
              <Link to={roomsLink} className="btn btn-sm btn-outline">Выбрать номер</Link>
            </div>
            <div className="room-checkboxes">
              {availableRooms.map((room) => (
                  <label key={room.id} className="room-checkbox">
                    <input
                        type="checkbox"
                        checked={selectedRooms.includes(room.id)}
                        onChange={() => toggleRoom(room.id)}
                    />
                    <span>
                  {ROOM_TYPE_LABELS[room.type.toLowerCase()] ?? room.type} — этаж {room.floor} — {room.price} BYN/ночь
                </span>
                  </label>
              ))}
            </div>
          </div>

          <div className="selected-rooms" style={{ marginBottom: '0.5rem' }}>
            <div className="form-label-row">
              <strong>Выбрано:</strong>
              <div>
                {selectedRooms.length > 0 && (
                    <button type="button" className="btn btn-sm btn-outline" onClick={clearSelection}>
                      Очистить всё
                    </button>
                )}
              </div>
            </div>
            {selectedRoomObjects.length ? (
                <ul style={{ listStyle: 'none', padding: 0, margin: '0.5rem 0' }}>
                  {selectedRoomObjects.map((r) => (
                      <li key={r.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.25rem 0' }}>
                        <span>{ROOM_TYPE_LABELS[r.type.toLowerCase()] ?? r.type} — {r.price} BYN</span>
                        <button
                            type="button"
                            className="btn btn-sm btn-danger"
                            onClick={() => toggleRoom(r.id)}
                        >
                          Убрать
                        </button>
                      </li>
                  ))}
                </ul>
            ) : (
                <span className="text-muted">нет</span>
            )}
          </div>

          <div className="booking-summary">
            <strong>Итого: {totalPrice.toLocaleString('ru-RU')} BYN</strong>
            {nights > 1 && <span className="text-muted"> ({nights} ночей)</span>}
          </div>

          <div className="form-group">
            <label>Способ оплаты</label>
            <select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}>
              <option value="card">Банковская карта</option>
              <option value="cash">Наличные при заезде</option>
              <option value="transfer">Банковский перевод</option>
            </select>
          </div>

          <button type="submit" className="btn btn-accent btn-lg btn-block" disabled={submitting}>
            {submitting ? 'Оформление...' : 'Подтвердить бронирование'}
          </button>
        </form>
      </div>
  );
}