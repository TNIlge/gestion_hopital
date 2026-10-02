import { Routes } from '@angular/router';
import { Appointment } from './composants/appointment/appointment';
import { Connexion } from './composants/connexion/connexion';
import { Landing } from './composants/landing/landing';
import { MedecinPlanning } from './composants/medecin/medecin';
import { Secretariat } from './composants/secretariat/secretariat';
import { roleGuard } from './core/role.guard';

export const routes: Routes = [
  { path: '', component: Landing, title: 'MediCare' },
  { path: 'demande', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: 'rendez-vous', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: 'appointment', component: Appointment, title: 'Prendre un rendez-vous' },
  { path: 'connexion', component: Connexion, title: 'Connexion' },
  {
    path: 'secretariat',
    component: Secretariat,
    canActivate: [roleGuard('SECRETAIRE')],
    title: 'Espace secrétariat',
  },
  {
    path: 'medecin',
    component: MedecinPlanning,
    canActivate: [roleGuard('MEDECIN')],
    title: 'Mon planning',
  },
  { path: '**', redirectTo: '' },
];
