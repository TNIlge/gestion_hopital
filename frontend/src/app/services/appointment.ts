import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, shareReplay } from 'rxjs';
import { AppointmentRequest, AppointmentResponse } from '../models/appointment-request';

interface ReferentielDepartement {
  nom: string;
  specialites: string[];
}

@Injectable({
  providedIn: 'root',
})
export class appointmentService {
  private readonly apiUrl = 'http://localhost:8080/api';

  // Le référentiel (départements + spécialités) est chargé une seule fois puis partagé
  private readonly referentiel$: Observable<ReferentielDepartement[]>;

  constructor(private http: HttpClient) {
    this.referentiel$ = this.http
      .get<ReferentielDepartement[]>(`${this.apiUrl}/v1/referentiel/specialites`)
      .pipe(shareReplay(1));
  }

  // 1. Récupération des départements depuis le backend
  getDepartments(): Observable<string[]> {
    return this.referentiel$.pipe(map((departements) => departements.map((d) => d.nom)));
  }

  // 2. Récupération des spécialités d'un département donné depuis le backend
  getSpecialtiesByDepartment(department: string): Observable<string[]> {
    return this.referentiel$.pipe(
      map((departements) => departements.find((d) => d.nom === department)?.specialites ?? []),
    );
  }

  // 3. Enregistrement de la demande de rendez-vous vers le backend
  createAppointment(payload: AppointmentRequest): Observable<AppointmentResponse> {
    return this.http.post<AppointmentResponse>(`${this.apiUrl}/v1/rendez-vous`, payload);
  }
}
