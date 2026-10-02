import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { ConsultationPlanning, Medecin, StatutPresence } from '../models';

/** Accès aux données de l'espace médecin. */
@Injectable({ providedIn: 'root' })
export class MedecinService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/medecin`;

  /** Connexion par matricule : 404 si le matricule est inconnu. */
  authentifier(matricule: string): Observable<Medecin> {
    return this.http.post<Medecin>(`${this.url}/auth`, { matricule });
  }

  getPlanning(matricule: string): Observable<ConsultationPlanning[]> {
    return this.http.get<ConsultationPlanning[]>(`${this.url}/${encodeURIComponent(matricule)}/planning`);
  }

  pointerPresence(id: number, presence: StatutPresence): Observable<ConsultationPlanning> {
    return this.http.put<ConsultationPlanning>(`${this.url}/rendez-vous/${id}/presence`, { presence });
  }
}
