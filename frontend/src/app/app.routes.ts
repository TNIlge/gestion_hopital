import { Routes } from '@angular/router';
import { Landing } from './pages/landing/landing';
import { Appointment } from './appointment/appointment';

export const routes: Routes = [
  { path: '', component: Landing, title: 'Accueil — MediCare' },
  { path: 'demande', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: 'rendez-vous', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: 'appointment', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: '**', redirectTo: '' },
];
