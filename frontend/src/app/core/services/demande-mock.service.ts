import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { delay, Observable, of, throwError } from 'rxjs';
import { Demande, Medecin, StatutDemande } from '../models';
import { memeCreneau } from '../referentiel';
import { DemandeService } from './demande.service';
import { DEMANDES_MOCK, MEDECINS_MOCK } from './mock-data';

const LATENCE_MS = 300;

/**
 * Implémentation en mémoire qui reproduit le comportement attendu de l'API,
 * y compris l'erreur 409 en cas de conflit d'agenda.
 */
@Injectable()
export class DemandeMockService extends DemandeService {
  private demandes: Demande[] = structuredClone(DEMANDES_MOCK);

  getDemandes(): Observable<Demande[]> {
    return of(structuredClone(this.demandes)).pipe(delay(LATENCE_MS));
  }

  changerStatut(id: number, statut: StatutDemande, motifRefus?: string): Observable<Demande> {
    const demande = this.trouver(id);
    if (!demande) return this.erreur(404, 'Demande introuvable');

    demande.statut = statut;
    demande.motifRefus = statut === 'REFUSEE' ? motifRefus : undefined;
    return of(structuredClone(demande)).pipe(delay(LATENCE_MS));
  }

  assignerMedecin(id: number, medecinId: number): Observable<Demande> {
    const demande = this.trouver(id);
    const medecin = MEDECINS_MOCK.find((m) => m.id === medecinId);
    if (!demande || !medecin) return this.erreur(404, 'Demande ou médecin introuvable');
    if (medecin.specialite !== demande.specialite) {
      return this.erreur(400, 'Ce médecin n\'est pas rattaché à la spécialité demandée');
    }

    const conflit = this.demandes.some(
      (d) =>
        d.id !== id &&
        d.statut === 'VALIDEE' &&
        d.medecin?.id === medecinId &&
        memeCreneau(d.dateSouhaitee, demande.dateSouhaitee),
    );
    if (conflit) {
      return this.erreur(409, `Dr ${medecin.nom} a déjà une consultation validée sur ce créneau`);
    }

    demande.medecin = { id: medecin.id, nom: medecin.nom, prenom: medecin.prenom };
    demande.statut = 'VALIDEE';
    demande.motifRefus = undefined;
    return of(structuredClone(demande)).pipe(delay(LATENCE_MS));
  }

  getMedecins(specialite?: string): Observable<Medecin[]> {
    const medecins = specialite ? MEDECINS_MOCK.filter((m) => m.specialite === specialite) : MEDECINS_MOCK;
    return of(structuredClone(medecins)).pipe(delay(LATENCE_MS));
  }

  private trouver(id: number): Demande | undefined {
    return this.demandes.find((d) => d.id === id);
  }

  private erreur(status: number, message: string): Observable<never> {
    return throwError(() => new HttpErrorResponse({ status, error: { message } })).pipe(delay(LATENCE_MS));
  }
}
