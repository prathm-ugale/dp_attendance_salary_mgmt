import { Routes } from '@angular/router';
import { SupervisorAttendanceComponent } from './supervisor-attendance/supervisor-attendance';
import { AdminLabourSummaryComponent } from './admin-labour-summary/admin-labour-summary';
import { HomeComponent } from './home/home';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'supervisor', component: SupervisorAttendanceComponent },
  { path: 'admin', component: AdminLabourSummaryComponent },
  { path: '**', redirectTo: '' }
];
