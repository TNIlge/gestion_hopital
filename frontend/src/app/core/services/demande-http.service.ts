import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Demande, Medecin, StatutDemande } from '../models';
import { DemandeService } from './demande.service';

@Injectable()
export class DemandeHttpService extends DemandeService {
  private readonly http = inject(HttpClient);

  getDemandes(): Observable<Demande[]> {
    return this.http.get<Demande[]>(`${API_URL}/demandes`);
  }

  changerStatut(id: number, statut: StatutDemande, motifRefus?: string): Observable<Demande> {
    return this.http.patch<Demande>(`${API_URL}/demandes/${id}/statut`, { statut, motifRefus });
  }

  assignerMedecin(id: number, medecinId: number): Observable<Demande> {
    return this.http.put<Demande>(`${API_URL}/demandes/${id}/medecin`, { medecinId });
  }

  getMedecins(specialite?: string): Observable<Medecin[]> {
    const params = specialite ? new HttpParams().set('specialite', specialite) : undefined;
    return this.http.get<Medecin[]>(`${API_URL}/medecins`, { params });
  }
}
