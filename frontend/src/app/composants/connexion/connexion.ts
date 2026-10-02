import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Role } from '../../core/models';
import { ACCUEIL_PAR_ROLE } from '../../core/role.guard';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-connexion',
  imports: [FormsModule, RouterLink],
  templateUrl: './connexion.html',
  styleUrl: './connexion.css',
})
export class Connexion implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly role = signal<Role>('MEDECIN');
  protected readonly matricule = signal('');
  protected readonly envoi = signal(false);
  protected readonly erreur = signal<string | null>(null);

  ngOnInit(): void {
    // Déjà connecté : on renvoie directement vers son espace
    const session = this.auth.session();
    if (session) this.router.navigateByUrl(ACCUEIL_PAR_ROLE[session.role]);
  }

  protected choisirRole(role: Role): void {
    this.role.set(role);
    this.erreur.set(null);
  }

  protected seConnecter(): void {
    if (this.role() === 'SECRETAIRE') {
      this.auth.connexionSecretariat();
      this.router.navigateByUrl(ACCUEIL_PAR_ROLE.SECRETAIRE);
      return;
    }

    if (!this.matricule().trim() || this.envoi()) return;
    this.envoi.set(true);
    this.erreur.set(null);
    this.auth.connexionMedecin(this.matricule()).subscribe({
      next: () => this.router.navigateByUrl(ACCUEIL_PAR_ROLE.MEDECIN),
      error: (e: HttpErrorResponse) => {
        this.envoi.set(false);
        this.erreur.set(
          e.status === 404
            ? 'Matricule inconnu. Vérifiez votre numéro de médecin.'
            : e.status === 0
              ? 'Serveur injoignable. Vérifiez que le backend est démarré.'
              : (e.error?.message ?? 'La connexion a échoué, veuillez réessayer.'),
        );
      },
    });
  }
}
