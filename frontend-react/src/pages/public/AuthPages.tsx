import { useState } from 'react';

import { Link, useNavigate, useSearchParams } from 'react-router-dom';

import { useForm } from 'react-hook-form';

import { zodResolver } from '@hookform/resolvers/zod';

import { useAuth } from '../../context/AuthContext';

import { ApiError } from '../../api/client';

import { ErrorAlert } from '../../components/ui/Alert';

import { loginSchema, registerSchema } from '../../lib/schemas';

import { z } from 'zod';



type LoginForm = z.infer<typeof loginSchema>;

type RegisterForm = z.infer<typeof registerSchema>;



export function LoginPage() {

  const { loginGuest } = useAuth();

  const navigate = useNavigate();

  const [params] = useSearchParams();

  const redirect = params.get('redirect') ?? '/profile';

  const [error, setError] = useState('');



  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginForm>({

    resolver: zodResolver(loginSchema),

  });



  const onSubmit = async (data: LoginForm) => {

    setError('');

    try {

      await loginGuest(data.email, data.password);

      navigate(redirect);

    } catch (err) {

      setError(err instanceof ApiError ? err.message : 'Ошибка входа');

    }

  };



  return (

    <div className="container page auth-page">

      <div className="auth-card card">

        <h1>Вход для гостей</h1>

        <p className="text-muted">Введите email и пароль</p>

        {error && <ErrorAlert message={error} />}

        <form onSubmit={handleSubmit(onSubmit)}>

          <div className="form-group">

            <label>Email</label>

            <input type="email" {...register('email')} />

            {errors.email && <span className="field-error">{errors.email.message}</span>}

          </div>

          <div className="form-group">

            <label>Пароль</label>

            <input type="password" {...register('password')} />

            {errors.password && <span className="field-error">{errors.password.message}</span>}

          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={isSubmitting}>

            {isSubmitting ? 'Вход...' : 'Войти'}

          </button>

        </form>

        <p className="auth-footer">

          Нет аккаунта? <Link to="/register">Зарегистрироваться</Link>

        </p>

        <p className="auth-footer">

          <Link to="/admin/login">Вход для администратора</Link>

        </p>

      </div>

    </div>

  );

}



export function RegisterPage() {

  const { registerGuest } = useAuth();

  const navigate = useNavigate();

  const [error, setError] = useState('');

  const [validationErrors, setValidationErrors] = useState<Record<string, string>>();



  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<RegisterForm>({

    resolver: zodResolver(registerSchema),

  });



  const onSubmit = async (data: RegisterForm) => {

    setError('');

    setValidationErrors(undefined);

    try {

      await registerGuest(data);

      navigate('/profile');

    } catch (err) {

      if (err instanceof ApiError) {

        setError(err.message);

        setValidationErrors(err.validationErrors);

      } else setError('Ошибка регистрации');

    }

  };



  return (

    <div className="container page auth-page">

      <div className="auth-card card">

        <h1>Регистрация гостя</h1>

        {error && <ErrorAlert message={error} validationErrors={validationErrors} />}

        <form onSubmit={handleSubmit(onSubmit)}>

          <div className="form-group">

            <label>ФИО *</label>

            <input {...register('fullName')} />

            {errors.fullName && <span className="field-error">{errors.fullName.message}</span>}

          </div>

          <div className="form-group">

            <label>Телефон *</label>

            <input {...register('phoneNumber')} />

            {errors.phoneNumber && <span className="field-error">{errors.phoneNumber.message}</span>}

          </div>

          <div className="form-group">

            <label>Email *</label>

            <input type="email" {...register('email')} />

            {errors.email && <span className="field-error">{errors.email.message}</span>}

          </div>

          <div className="form-group">

            <label>Пароль *</label>

            <input type="password" {...register('password')} />

            {errors.password && <span className="field-error">{errors.password.message}</span>}

          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={isSubmitting}>

            {isSubmitting ? 'Регистрация...' : 'Зарегистрироваться'}

          </button>

        </form>

        <p className="auth-footer">

          Уже есть аккаунт? <Link to="/login">Войти</Link>

        </p>

      </div>

    </div>

  );

}



export function AdminLoginPage() {

  const { loginAdmin, isAdmin } = useAuth();

  const navigate = useNavigate();

  const [password, setPassword] = useState('');

  const [error, setError] = useState('');

  const [loading, setLoading] = useState(false);



  if (isAdmin) {

    navigate('/admin');

    return null;

  }



  const handleSubmit = async (e: React.FormEvent) => {

    e.preventDefault();

    setLoading(true);

    setError('');

    try {

      await loginAdmin(password);

      navigate('/admin');

    } catch (err) {

      setError(err instanceof ApiError ? err.message : 'Неверный пароль администратора');

    } finally {

      setLoading(false);

    }

  };



  return (

    <div className="container page auth-page">

      <div className="auth-card card">

        <h1>Вход администратора</h1>

        <p className="text-muted">Введите пароль администратора</p>

        {error && <ErrorAlert message={error} />}

        <form onSubmit={handleSubmit}>

          <div className="form-group">

            <label>Пароль</label>

            <input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} />

          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>

            {loading ? 'Вход...' : 'Войти в панель'}

          </button>

        </form>

        <p className="auth-footer"><Link to="/">На главную</Link></p>

      </div>

    </div>

  );

}

