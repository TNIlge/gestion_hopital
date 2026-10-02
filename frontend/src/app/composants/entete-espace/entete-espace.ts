import { Component, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

/** En-tête des espaces connectés (secrétariat, médecin). */
@Component({
  selector: 'app-entete-espace',
  imports: [RouterLink],
  template: `
    <nav class="navbar border-bottom bg-white py-3">
      <div class="container-fluid page" [style.max-width]="largeurMax()">
        <a class="navbar-brand d-flex align-items-center gap-2 m-0" routerLink="/">
          @if (logoDisponible()) {
            <img src="logo.png" alt="Logo de l'hôpital" class="logo" (error)="logoDisponible.set(false)" />
          }
          <span class="fw-semibold">{{ titre() }}</span>
        </a>
        <div class="d-flex align-items-center gap-3">
          @if (auth.session(); as s) {
            <span class="utilisateur d-none d-sm-inline">
              <i class="bi bi-person-circle me-1"></i>
              {{ s.role === 'MEDECIN' ? 'Dr ' + s.prenom + ' ' + s.nom : s.nom }}
            </span>
          }
          <button type="button" class="btn btn-outline-brand btn-sm px-3" (click)="deconnexion()">
            <i class="bi bi-box-arrow-left me-2"></i>Déconnexion
          </button>
        </div>
      </div>
    </nav>
  `,
  styles: `
    .page {
      padding-inline: 1.5rem;
    }

    .logo {
      width: 36px;
      height: 36px;
      object-fit: contain;
    }

    .utilisateur {
      color: var(--muted);
      font-size: 0.9rem;
    }
  `,
})
export class EnteteEspace {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly titre = input.required<string>();
  /** Aligne l'en-tête sur la largeur du contenu de la page */
  readonly largeurMax = input('1440px');

  protected readonly logoDisponible = signal(true);

  protected deconnexion(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
