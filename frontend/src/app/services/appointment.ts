import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppointmentRequest, AppointmentResponse } from '../models/appointment-request';

@Injectable({
  providedIn: 'root',
})
export class appointmentService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // 1. Récupération des départements depuis le backend
  getDepartments(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/appointments/departments`);
  }

  // 2. Récupération des spécialités d'un département donné depuis le backend
  getSpecialtiesByDepartment(department: string): Observable<string[]> {
    return this.http.get<string[]>(
      `${this.apiUrl}/appointments/departments/${encodeURIComponent(department)}/specialties`
    );
  }

  // 3. Enregistrement de la demande de rendez-vous vers le backend
  createAppointment(payload: AppointmentRequest): Observable<AppointmentResponse> {
    return this.http.post<AppointmentResponse>(`${this.apiUrl}/v1/rendez-vous`, payload);
  }
}