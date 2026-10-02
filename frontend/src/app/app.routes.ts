import { Routes } from '@angular/router';
import { Appointment } from './composants/appointment/appointment';
import { Landing } from './composants/landing/landing';

export const routes: Routes = [
  { path: '', component: Landing, title: 'MediCare' },
  { path: 'demande', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: 'rendez-vous', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: 'appointment', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: '**', redirectTo: '' },
];
