import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, shareReplay } from 'rxjs';
import { AppointmentRequest, AppointmentResponse } from '../models/appointment-request';

export interface ReferentielDepartement {
  nom: string;
  specialites: string[];
}

export interface SuiviDemandeResponse {
  numeroDossier: string;
  statut: string; // 'En attente', 'Acceptée', 'Validée', 'Refusée', 'Déclinée', 'Dépassée'
  nom: string;
  prenom: string;
  departement: string;
  specialite: string;
  dateSouhaitee: string;
  dateCreation: string;
  motifRefus?: string | null;
  medecinNom?: string | null;
  medecinSpecialite?: string | null;
  dateConsultation?: string | null;
  heureConsultation?: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class appointmentService {
  private readonly baseUrl = 'http://localhost:8080/api/v1';
  private referentiel$?: Observable<ReferentielDepartement[]>;

  constructor(private http: HttpClient) {}

  // 1. Récupération du référentiel complet (départements et spécialités) depuis le backend Spring Boot
  getReferentiel(): Observable<ReferentielDepartement[]> {
    if (!this.referentiel$) {
      this.referentiel$ = this.http
        .get<ReferentielDepartement[]>(`${this.baseUrl}/referentiel/specialites`)
        .pipe(shareReplay(1));
    }
    return this.referentiel$;
  }

  // 2. Récupération des noms de départements depuis le backend
  getDepartments(): Observable<string[]> {
    return this.getReferentiel().pipe(
      map((departments) => departments.map((d) => d.nom))
    );
  }

  // 3. Récupération des spécialités d'un département donné depuis le backend
  getSpecialtiesByDepartment(departmentName: string): Observable<string[]> {
    return this.getReferentiel().pipe(
      map((departments) => {
        const found = departments.find(
          (d) => d.nom.trim().toLowerCase() === departmentName.trim().toLowerCase()
        );
        return found ? found.specialites : [];
      })
    );
  }

  // 4. Enregistrement de la demande de rendez-vous vers le backend
  createAppointment(payload: AppointmentRequest): Observable<AppointmentResponse> {
    return this.http.post<AppointmentResponse>(`${this.baseUrl}/rendez-vous`, payload);
  }

  // 5. CARE-105 : Suivre l'état d'avancement d'un dossier par son numéro unique
  getSuiviDemande(numeroDossier: string): Observable<SuiviDemandeResponse> {
    return this.http.get<SuiviDemandeResponse>(
      `${this.baseUrl}/rendez-vous/suivi/${encodeURIComponent(numeroDossier.trim())}`
    );
  }
}