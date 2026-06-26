import { Injectable } from '@angular/core';
import { ApiClientService } from './api-client.service';
import {
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
} from '../models/types';

type PageParams = Record<string, string | number | undefined>;

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private api: ApiClientService) {}

  auth = {
    register: (data: RegisterRequest) => this.api.post<AuthResponse>('/auth/register', data),
    login: (data: LoginRequest) => this.api.post<AuthResponse>('/auth/login', data),
    adminLogin: (password: string) => this.api.post<AuthResponse>('/auth/admin/login', { password }),
    me: () => this.api.get<Guest>('/auth/me'),
    deleteAccount: () => this.api.delete('/auth/account'),
    changePassword: (data: ChangePasswordRequest) => this.api.post('/auth/change-password', data),
  };

  guest = {
    list: (p: PageParams = {}) => this.api.get<Page<Guest>>('/guest', { page: 0, size: 10, ...p }),
    get: (id: number) => this.api.get<Guest>(`/guest/${id}`),
    create: (data: GuestRequest) => this.api.post<Guest>('/guest', data),
    update: (id: number, data: GuestRequest) => this.api.put<Guest>(`/guest/${id}`, data),
    delete: (id: number) => this.api.delete(`/guest/${id}`),
  };

  room = {
    list: (p: PageParams = {}) => this.api.get<Page<Room>>('/room', { page: 0, size: 100, ...p }),
    get: (id: number) => this.api.get<Room>(`/room/${id}`),
    create: (data: RoomRequest) => this.api.post<Room>('/room', data),
    update: (id: number, data: RoomRequest) => this.api.put<Room>(`/room/${id}`, data),
    delete: (id: number) => this.api.delete(`/room/${id}`),
  };

  booking = {
    list: (p: PageParams = {}) => this.api.get<Page<Booking>>('/booking', { page: 0, size: 100, ...p }),
    get: (id: number) => this.api.get<Booking>(`/booking/${id}`),
    create: (data: BookingRequest) => this.api.post<Booking>('/booking', data),
    update: (id: number, data: BookingRequest) => this.api.put<Booking>(`/booking/${id}`, data),
    delete: (id: number) => this.api.delete(`/booking/${id}`),
  };

  payment = {
    list: (p: PageParams = {}) => this.api.get<Page<Payment>>('/payment', { page: 0, size: 100, ...p }),
    get: (id: number) => this.api.get<Payment>(`/payment/${id}`),
    create: (data: PaymentRequest) => this.api.post<Payment>('/payment', data),
    update: (id: number, data: PaymentRequest) => this.api.put<Payment>(`/payment/${id}`, data),
    delete: (id: number) => this.api.delete(`/payment/${id}`),
  };

  service = {
    list: (p: PageParams = {}) => this.api.get<Page<Service>>('/service', { page: 0, size: 100, ...p }),
    get: (id: number) => this.api.get<Service>(`/service/${id}`),
    create: (data: ServiceRequest) => this.api.post<Service>('/service', data),
    update: (id: number, data: ServiceRequest) => this.api.put<Service>(`/service/${id}`, data),
    delete: (id: number) => this.api.delete(`/service/${id}`),
  };

  employee = {
    list: (p: PageParams = {}) => this.api.get<Page<Employee>>('/employee', { page: 0, size: 100, ...p }),
    get: (id: number) => this.api.get<Employee>(`/employee/${id}`),
    create: (data: EmployeeRequest) => this.api.post<Employee>('/employee', data),
    update: (id: number, data: EmployeeRequest) => this.api.put<Employee>(`/employee/${id}`, data),
    delete: (id: number) => this.api.delete(`/employee/${id}`),
  };

  position = {
    list: (p: PageParams = {}) => this.api.get<Page<Position>>('/position', { page: 0, size: 100, ...p }),
    get: (id: number) => this.api.get<Position>(`/position/${id}`),
    create: (data: PositionRequest) => this.api.post<Position>('/position', data),
    update: (id: number, data: PositionRequest) => this.api.put<Position>(`/position/${id}`, data),
    delete: (id: number) => this.api.delete(`/position/${id}`),
  };
}
