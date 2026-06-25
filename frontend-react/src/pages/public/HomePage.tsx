import { useState } from 'react';

import { Link, useNavigate } from 'react-router-dom';



const FEATURES = [

  { title: 'Комфортные номера', desc: 'Стандарт, люкс и семейные номера на любой вкус' },

  { title: 'SPA и услуги', desc: 'Массаж, завтрак в номер, трансфер и многое другое' },

  { title: 'Удобная оплата', desc: 'Карта, наличные, онлайн-оплата при бронировании' },

  { title: 'Онлайн-бронирование', desc: 'Мгновенное подтверждение брони без звонков' },

];



export function HomePage() {

  const navigate = useNavigate();

  const [checkIn, setCheckIn] = useState('');

  const [duration, setDuration] = useState('1 ночь');

  const [guests, setGuests] = useState(2);



  const handleSearch = (e: React.FormEvent) => {

    e.preventDefault();

    const params = new URLSearchParams();

    if (checkIn) params.set('checkIn', checkIn);

    if (duration) params.set('duration', duration);

    params.set('guests', String(guests));

    navigate(`/rooms?${params}`);

  };



  return (

    <>

      <section className="hero">

        <div className="hero-overlay" />

        <div className="container hero-content">

          <h1>Найдите идеальный номер для вашего отдыха</h1>

          <p>Комфортабельные номера, SPA-услуги и профессиональный сервис</p>

          <form className="search-bar" onSubmit={handleSearch}>

            <div className="search-field">

              <label>Дата заезда</label>

              <input type="date" value={checkIn} onChange={(e) => setCheckIn(e.target.value)} />

            </div>

            <div className="search-field">

              <label>Длительность</label>

              <select value={duration} onChange={(e) => setDuration(e.target.value)}>

                <option>1 ночь</option>

                <option>2 ночи</option>

                <option>3 ночи</option>

                <option>1 неделя</option>

              </select>

            </div>

            <div className="search-field">

              <label>Гости</label>

              <select value={guests} onChange={(e) => setGuests(Number(e.target.value))}>

                {[1, 2, 3, 4, 5, 6].map((n) => (

                  <option key={n} value={n}>{n} {n === 1 ? 'гость' : 'гостей'}</option>

                ))}

              </select>

            </div>

            <button type="submit" className="btn btn-accent btn-search">Найти номера</button>

          </form>

        </div>

      </section>



      <section className="section container">

        <h2 className="section-title">Почему выбирают нас</h2>

        <div className="features-grid">

          {FEATURES.map((f, i) => (

            <div key={f.title} className="feature-card">

              <span className="feature-index">{String(i + 1).padStart(2, '0')}</span>

              <h3>{f.title}</h3>

              <p>{f.desc}</p>

            </div>

          ))}

        </div>

      </section>



      <section className="section section-alt">

        <div className="container cta-block">

          <div>

            <h2>Готовы забронировать?</h2>

            <p>Выберите номер и оформите бронирование за несколько минут</p>

          </div>

          <Link to="/booking" className="btn btn-accent btn-lg">Забронировать сейчас</Link>

        </div>

      </section>

    </>

  );

}

