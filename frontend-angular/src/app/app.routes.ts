import { Routes } from '@angular/router';
import { PublicLayoutComponent, AdminLayoutComponent } from './components/layout/layout.component';
import { adminGuard, guestGuard } from './core/guards/auth.guards';
import { HomeComponent } from './pages/public/home.component';
import { RoomsComponent } from './pages/public/rooms.component';
import { BookingComponent } from './pages/public/booking.component';
import { ServicesComponent } from './pages/public/services.component';
import { LoginComponent, RegisterComponent, AdminLoginComponent } from './pages/public/auth.component';
import { GuestProfileComponent } from './pages/public/guest-profile.component';
import { DashboardComponent } from './pages/admin/dashboard.component';
import { GuestAdminComponent } from './pages/admin/guest-admin.component';
import { RoomAdminComponent } from './pages/admin/room-admin.component';
import { BookingAdminComponent } from './pages/admin/booking-admin.component';
import { PaymentAdminComponent } from './pages/admin/payment-admin.component';
import { ServiceAdminComponent } from './pages/admin/service-admin.component';
import { EmployeeAdminComponent } from './pages/admin/employee-admin.component';
import { PositionAdminComponent } from './pages/admin/position-admin.component';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'rooms', component: RoomsComponent },
      { path: 'booking', component: BookingComponent },
      { path: 'services', component: ServicesComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'profile', component: GuestProfileComponent, canActivate: [guestGuard] },
    ],
  },
  { path: 'admin/login', component: AdminLoginComponent },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],
    children: [
      { path: '', component: DashboardComponent },
      { path: 'guests', component: GuestAdminComponent },
      { path: 'rooms', component: RoomAdminComponent },
      { path: 'bookings', component: BookingAdminComponent },
      { path: 'payments', component: PaymentAdminComponent },
      { path: 'services', component: ServiceAdminComponent },
      { path: 'employees', component: EmployeeAdminComponent },
      { path: 'positions', component: PositionAdminComponent },
    ],
  },
];
