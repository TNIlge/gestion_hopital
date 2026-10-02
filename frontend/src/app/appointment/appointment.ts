import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { appointmentService } from '../services/appointment';
import { AppointmentRequest } from '../models/appointment-request';

export interface ConfirmedAppointment {
  numeroDossier: string;
  nom: string;
  prenom: string;
  dateNaissance: string;
  nssMasque: string;
  departement: string;
  specialite: string;
  dateSouhaitee: string;
  motif: string;
  dateCreation?: string;
  statut?: string;
}

@Component({
  selector: 'app-appointment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './appointment.html',
  styleUrl: './appointment.css',
})
export class Appointment implements OnInit {
  appointmentForm!: FormGroup;

  departments: string[] = [];
  specialties: string[] = [];

  successMessage!: string;
  errorMessage!: string;
  loadingSpecialties = false;
  numeroDossier?: string;
  submitting = false;

  // Données de confirmation affichées après succès
  confirmedAppointment?: ConfirmedAppointment;
  dossierCopie = false;

  constructor(
    private fb: FormBuilder,
    private appointmentService: appointmentService
  ) {}

  get nssError(): { attendu: string; saisi: string } | null {
    return this.appointmentForm?.errors?.['nssInvalide'] || null;
  }

  ngOnInit(): void {
    this.appointmentForm = this.fb.group(
      {
        lastName: ['', [Validators.required, Validators.minLength(2)]],
        firstName: ['', [Validators.required, Validators.minLength(2)]],
        birthDate: ['', Validators.required],
        socialSecurityNumber: ['', Validators.required],
        department: ['', Validators.required],
        specialty: [{ value: '', disabled: true }, Validators.required],
        reason: ['', Validators.required],
        desiredDate: ['', Validators.required],
      },
      {
        validators: [this.nssCorrespondanceValidator],
      }
    );

    this.loadDepartments();
    this.setupDepartmentChangeListener();
  }

  // 1. Appel API pur pour charger les départements depuis le backend Spring Boot
  loadDepartments(): void {
    this.appointmentService.getDepartments().subscribe({
      next: (data) => {
        this.departments = data;
        this.errorMessage = '';
      },
      error: (error) => {
        this.errorMessage = "Impossible de charger les départements depuis le serveur.";
        console.error("Erreur chargement départements", error);
      },
    });
  }

  // 2. Appel API pur pour charger les spécialités associées au département
  setupDepartmentChangeListener(): void {
    this.appointmentForm.get('department')?.valueChanges.subscribe({
      next: (department) => {
        const specialtyControl = this.appointmentForm.get('specialty');
        specialtyControl?.reset();

        if (department) {
          specialtyControl?.enable();
          this.loadingSpecialties = true;

          this.appointmentService.getSpecialtiesByDepartment(department).subscribe({
            next: (data) => {
              this.specialties = data;
              this.loadingSpecialties = false;
            },
            error: (error) => {
              this.errorMessage = "Impossible de charger les spécialités pour ce département.";
              console.error("Erreur chargement spécialités", error);
              this.specialties = [];
              this.loadingSpecialties = false;
            },
          });
        } else {
          specialtyControl?.disable();
          this.specialties = [];
        }
      },
    });
  }

  // 3. Validateur réactif : vérifie la concordance du N° de Sécurité Sociale avec nom-prenom-YYYYMMDD
  private nssCorrespondanceValidator: ValidatorFn = (
    control: AbstractControl
  ): ValidationErrors | null => {
    const lastName = control.get('lastName')?.value;
    const firstName = control.get('firstName')?.value;
    const birthDate = control.get('birthDate')?.value;
    const nss = control.get('socialSecurityNumber')?.value;

    if (!lastName || !firstName || !birthDate || !nss) {
      return null;
    }

    const nomNorm = this.normaliserChaine(lastName);
    const prenomNorm = this.normaliserChaine(firstName);
    const dateNorm = (birthDate || '').replace(/-/g, '');
    const nssAttendu = `${nomNorm}-${prenomNorm}-${dateNorm}`;

    const nssNorm = this.normaliserChaine(nss);

    if (nssNorm !== nssAttendu) {
      return {
        nssInvalide: {
          attendu: nssAttendu,
          saisi: nss,
        },
      };
    }

    return null;
  };

  // 4. Masquage sécurisé du numéro de sécurité sociale pour affichage écran
  masquerNss(nss: string): string {
    if (!nss) return '••••••••';
    const parts = nss.split('-');
    if (parts.length === 3) {
      const nom = parts[0];
      const prenom = parts[1];
      const date = parts[2];
      const nomMasque = nom.length > 2 ? nom.substring(0, 2) + '••••' : '••••';
      const prenomMasque = prenom.length > 2 ? prenom.substring(0, 2) + '••••' : '••••';
      const dateMasquee = '••••' + date.slice(-4);
      return `${nomMasque}-${prenomMasque}-${dateMasquee}`;
    }
    if (nss.length > 4) {
      return '•••• •••• •••• ' + nss.slice(-4);
    }
    return '••••••••';
  }

  // 5. Soumission du formulaire
  submit(): void {
    if (this.appointmentForm.invalid) {
      this.appointmentForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.successMessage = '';
    this.errorMessage = '';

    const formVal = this.appointmentForm.getRawValue();

    const payload: AppointmentRequest = {
      nom: formVal.lastName.trim(),
      prenom: formVal.firstName.trim(),
      dateNaissance: formVal.birthDate,
      numeroSecuriteSociale: formVal.socialSecurityNumber.trim(),
      departement: formVal.department,
      specialite: formVal.specialty,
      dateSouhaitee: formVal.desiredDate,
      motif: (formVal.reason || '').trim(),
    };

    this.appointmentService.createAppointment(payload).subscribe({
      next: (response) => {
        this.submitting = false;
        const numDossier = response?.numeroDossier || 'RDV-' + new Date().getFullYear() + '-VALIDE';
        this.numeroDossier = numDossier;
        this.successMessage =
          response?.message || "Votre demande de rendez-vous a été enregistrée avec succès !";

        // Génère l'objet de confirmation pour l'écran récapitulatif
        this.confirmedAppointment = {
          numeroDossier: numDossier,
          nom: payload.nom,
          prenom: payload.prenom,
          dateNaissance: payload.dateNaissance,
          nssMasque: this.masquerNss(payload.numeroSecuriteSociale),
          departement: payload.departement,
          specialite: payload.specialite,
          dateSouhaitee: payload.dateSouhaitee,
          motif: payload.motif || 'Consultation médicale',
          dateCreation: response?.dateCreation || new Date().toISOString(),
          statut: response?.statut || 'En attente de validation',
        };

        this.appointmentForm.reset();
        this.specialties = [];
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: (error) => {
        this.submitting = false;
        if (error?.error?.message) {
          this.errorMessage = error.error.message;
        } else if (error?.status === 0) {
          this.errorMessage =
            "Impossible de contacter le serveur backend (http://localhost:8080). Vérifiez que Spring Boot est bien démarré.";
        } else {
          this.errorMessage = "Une erreur est survenue lors de l'enregistrement de votre rendez-vous.";
        }
        console.error("Erreur soumission", error);
      },
    });
  }

  // 6. Réinitialisation pour nouveau rendez-vous
  nouveauRendezVous(): void {
    this.confirmedAppointment = undefined;
    this.numeroDossier = undefined;
    this.successMessage = '';
    this.errorMessage = '';
    this.appointmentForm.reset();
    this.specialties = [];
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // 7. Copier le numéro de dossier
  copierNumeroDossier(): void {
    if (this.confirmedAppointment?.numeroDossier) {
      navigator.clipboard?.writeText(this.confirmedAppointment.numeroDossier);
      this.dossierCopie = true;
      setTimeout(() => (this.dossierCopie = false), 2500);
    }
  }

  // 8. Imprimer la confirmation
  imprimerRecapitulatif(): void {
    window.print();
  }

  private normaliserChaine(str: string): string {
    if (!str) return '';
    return str
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-z0-9-]/g, '');
  }
}