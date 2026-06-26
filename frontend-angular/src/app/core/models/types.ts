export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ChangePasswordRequest {
  newPassword: string;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string>;
}

export interface Guest {
  id: number;
  fullName: string;
  phoneNumber: string;
  email: string;
  status: string;
  serviceIds?: number[];
}

export interface GuestRequest {
  fullName: string;
  phoneNumber: string;
  email: string;
  status: string;
  password?: string;
  serviceIds?: number[];
}

export interface AuthResponse {
  token: string;
  role: 'GUEST' | 'ADMIN';
  guest?: Guest;
}

export interface LoginRequest {
  email: string;
  password?: string;
}

export interface RegisterRequest {
  fullName: string;
  phoneNumber: string;
  email: string;
  password: string;
}

export interface Room {
  id: number;
  floor: number;
  type: string;
  status: string;
  price: number;
}

export interface RoomRequest {
  floor: number;
  type: string;
  status: string;
  price: number;
  employeeIds?: number[];
  bookingIds?: number[];
}

export interface Booking {
  id: number;
  price: number;
  status: string;
  checkInDate: string;
  duration: string;
  guest?: Guest;
  rooms?: Room[];
  payments?: Payment[];
}

export interface BookingRequest {
  price: number;
  status: string;
  checkInDate: string;
  duration: string;
  guestId?: number;
  roomIds?: number[];
  paymentIds?: number[];
}

export interface Payment {
  id: number;
  status: string;
  amount: number;
  paymentDate: string;
  paymentMethod: string;
  bookingIds?: number[];
  serviceIds?: number[];
}

export interface PaymentRequest {
  status: string;
  amount: number;
  paymentDate: string;
  paymentMethod: string;
  bookingIds?: number[];
  serviceIds?: number[];
}

export interface Service {
  id: number;
  title: string;
  description: string;
  price: number;
  duration: string;
  guestIds?: number[];
  employeeIds?: number[];
}

export interface ServiceRequest {
  title: string;
  description: string;
  price: number;
  duration: string;
  guestIds?: number[];
  employeeIds?: number[];
}

export interface Employee {
  id: number;
  fullName: string;
  experience: string;
  schedule: string;
  phoneNumber: string;
  positionIds?: number[];
  roomIds?: number[];
  serviceIds?: number[];
}

export interface EmployeeRequest {
  fullName: string;
  experience: string;
  schedule: string;
  phoneNumber: string;
  positionIds?: number[];
  roomIds?: number[];
  serviceIds?: number[];
}

export interface Position {
  id: number;
  title: string;
  salary: number;
  responsibilities: string;
  employeeIds?: number[];
}

export interface PositionRequest {
  title: string;
  salary: number;
  responsibilities: string;
  employeeIds?: number[];
}

export interface PageInfo {
  page: number;
  totalPages: number;
  totalElements: number;
}

export interface MultiSelectOption {
  id: number;
  label: string;
}
