import { computed, inject, Injectable, signal } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Session } from '../models';
import { MedecinService } from './medecin.service';

const CLE_SESSION = 'hopital.session';

/**
 * Connexion du personnel. Les patients ne se connectent pas : ils suivent leur
 * demande avec leur numéro de dossier.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly medecinService = inject(MedecinService);

  readonly session = signal<Session | null>(lireSession());
  readonly estConnecte = computed(() => this.session() !== null);

  /** Médecin : vérification du matricule par l'API (404 si inconnu). */
  connexionMedecin(matricule: string): Observable<Session> {
    return this.medecinService.authentifier(matricule.trim()).pipe(
      map((m) => this.ouvrir({ role: 'MEDECIN', nom: m.nom, prenom: m.prenom, medecinId: m.id, matricule: m.matricule })),
    );
  }

  /**
   * Secrétariat : l'API ne fournit pas encore d'authentification pour ce rôle,
   * la session est donc ouverte côté front uniquement.
   */
  connexionSecretariat(): Session {
    return this.ouvrir({ role: 'SECRETAIRE', nom: 'Secrétariat', prenom: '' });
  }

  logout(): void {
    this.session.set(null);
    ecrireSession(null);
  }

  private ouvrir(session: Session): Session {
    this.session.set(session);
    ecrireSession(session);
    return session;
  }
}

// La session est gardée le temps de l'onglet ; le stockage peut être indisponible (navigation privée)
function lireSession(): Session | null {
  try {
    const brut = sessionStorage.getItem(CLE_SESSION);
    return brut ? (JSON.parse(brut) as Session) : null;
  } catch {
    return null;
  }
}

function ecrireSession(session: Session | null): void {
  try {
    if (session) sessionStorage.setItem(CLE_SESSION, JSON.stringify(session));
    else sessionStorage.removeItem(CLE_SESSION);
  } catch {
    // Sans stockage, la session reste valable jusqu'au rechargement de la page
  }
}
