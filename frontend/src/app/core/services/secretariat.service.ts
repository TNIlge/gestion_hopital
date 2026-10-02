import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Affectation, Demande, Medecin } from '../models';

/**
 * Accès aux données du secrétariat.
 * Les erreurs métier arrivent en HttpErrorResponse avec un corps { status, error, message }.
 */
@Injectable({ providedIn: 'root' })
export class SecretariatService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/secretariat`;

  getDemandes(): Observable<Demande[]> {
    return this.http.get<Demande[]>(`${this.url}/demandes`);
  }

  refuser(id: number, motifRefus: string): Observable<Demande> {
    return this.http.patch<Demande>(`${this.url}/demandes/${id}/refuser`, { motifRefus });
  }

  getMedecins(specialite: string): Observable<Medecin[]> {
    return this.http.get<Medecin[]>(`${this.url}/medecins`, { params: new HttpParams().set('specialite', specialite) });
  }

  affecter(id: number, affectation: Affectation): Observable<Demande> {
    return this.http.post<Demande>(`${this.url}/demandes/${id}/affecter`, affectation);
  }
}
