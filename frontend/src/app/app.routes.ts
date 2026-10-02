import { Routes } from '@angular/router';
import { Landing } from './pages/landing/landing';
import { Secretariat } from './pages/secretariat/secretariat';

export const routes: Routes = [
  { path: '', component: Landing, title: 'Prise de rendez-vous' },
  { path: 'secretariat', component: Secretariat, title: 'Secrétariat — Demandes de rendez-vous' },
  { path: '**', redirectTo: '' },
];
