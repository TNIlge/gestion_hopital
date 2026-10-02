import { Observable } from 'rxjs';
import { Demande, Medecin, StatutDemande } from '../models';

/**
 * Contrat d'accès aux demandes. Les composants n'injectent que cette classe ;
 * l'implémentation (mock ou HTTP) est choisie dans app.config.ts via USE_MOCK.
 *
 * Erreurs : un conflit d'agenda est signalé par une HttpErrorResponse de statut 409.
 */
export abstract class DemandeService {
  abstract getDemandes(): Observable<Demande[]>;

  abstract changerStatut(id: number, statut: StatutDemande, motifRefus?: string): Observable<Demande>;

  abstract assignerMedecin(id: number, medecinId: number): Observable<Demande>;

  abstract getMedecins(specialite?: string): Observable<Medecin[]>;
}
