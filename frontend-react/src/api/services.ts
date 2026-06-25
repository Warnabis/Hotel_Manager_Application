import { api } from './client';
import type {
  AuthResponse,
  Booking,
  BookingRequest,
  ChangePasswordRequest,
  Employee,
  EmployeeRequest,
  Guest,
  GuestRequest,
  LoginRequest,
  Page,
  Payment,
  PaymentRequest,
  Position,
  PositionRequest,
  RegisterRequest,
  Room,
  RoomRequest,
  Service,
  ServiceRequest,
} from './types';

type PageParams = Record<string, string | number | undefined>;

export const authApi = {
  register: (data: RegisterRequest) => api.post<AuthResponse>('/auth/register', data),
  login: (data: LoginRequest) => api.post<AuthResponse>('/auth/login', data),
  adminLogin: (password: string) => api.post<AuthResponse>('/auth/admin/login', { password }),
  me: () => api.get<Guest>('/auth/me'),
  deleteAccount: () => api.delete('/auth/account'),
  changePassword: (data: ChangePasswordRequest) => api.post('/auth/change-password', data),
};

export const guestApi = {
  list: (p: PageParams = {}) => api.get<Page<Guest>>('/guest', { page: 0, size: 10, ...p }),
  get: (id: number) => api.get<Guest>(`/guest/${id}`),
  create: (data: GuestRequest) => api.post<Guest>('/guest', data),
  update: (id: number, data: GuestRequest) => api.put<Guest>(`/guest/${id}`, data),
  delete: (id: number) => api.delete(`/guest/${id}`),
};

export const roomApi = {
  list: (p: PageParams = {}) => api.get<Page<Room>>('/room', { page: 0, size: 100, ...p }),
  get: (id: number) => api.get<Room>(`/room/${id}`),
  create: (data: RoomRequest) => api.post<Room>('/room', data),
  update: (id: number, data: RoomRequest) => api.put<Room>(`/room/${id}`, data),
  delete: (id: number) => api.delete(`/room/${id}`),
};

export const bookingApi = {
  list: (p: PageParams = {}) => api.get<Page<Booking>>('/booking', { page: 0, size: 100, ...p }),
  get: (id: number) => api.get<Booking>(`/booking/${id}`),
  create: (data: BookingRequest) => api.post<Booking>('/booking', data),
  update: (id: number, data: BookingRequest) => api.put<Booking>(`/booking/${id}`, data),
  delete: (id: number) => api.delete(`/booking/${id}`),
};

export const paymentApi = {
  list: (p: PageParams = {}) => api.get<Page<Payment>>('/payment', { page: 0, size: 100, ...p }),
  get: (id: number) => api.get<Payment>(`/payment/${id}`),
  create: (data: PaymentRequest) => api.post<Payment>('/payment', data),
  update: (id: number, data: PaymentRequest) => api.put<Payment>(`/payment/${id}`, data),
  delete: (id: number) => api.delete(`/payment/${id}`),
};

export const serviceApi = {
  list: (p: PageParams = {}) => api.get<Page<Service>>('/service', { page: 0, size: 100, ...p }),
  get: (id: number) => api.get<Service>(`/service/${id}`),
  create: (data: ServiceRequest) => api.post<Service>('/service', data),
  update: (id: number, data: ServiceRequest) => api.put<Service>(`/service/${id}`, data),
  delete: (id: number) => api.delete(`/service/${id}`),
};

export const employeeApi = {
  list: (p: PageParams = {}) => api.get<Page<Employee>>('/employee', { page: 0, size: 100, ...p }),
  get: (id: number) => api.get<Employee>(`/employee/${id}`),
  create: (data: EmployeeRequest) => api.post<Employee>('/employee', data),
  update: (id: number, data: EmployeeRequest) => api.put<Employee>(`/employee/${id}`, data),
  delete: (id: number) => api.delete(`/employee/${id}`),
};

export const positionApi = {
  list: (p: PageParams = {}) => api.get<Page<Position>>('/position', { page: 0, size: 100, ...p }),
  get: (id: number) => api.get<Position>(`/position/${id}`),
  create: (data: PositionRequest) => api.post<Position>('/position', data),
  update: (id: number, data: PositionRequest) => api.put<Position>(`/position/${id}`, data),
  delete: (id: number) => api.delete(`/position/${id}`),
};