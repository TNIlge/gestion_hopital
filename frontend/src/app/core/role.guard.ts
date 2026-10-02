import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Role } from './models';
import { AuthService } from './services/auth.service';

/** Réserve une route à un rôle ; sinon renvoie vers la page de connexion. */
export function roleGuard(role: Role): CanActivateFn {
  return () => {
    const session = inject(AuthService).session();
    return session?.role === role ? true : inject(Router).createUrlTree(['/connexion']);
  };
}

/** Page d'accueil de chaque rôle après connexion. */
export const ACCUEIL_PAR_ROLE: Record<Role, string> = {
  SECRETAIRE: '/secretariat',
  MEDECIN: '/medecin',
};
