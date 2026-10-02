import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { appointmentService, SuiviDemandeResponse } from '../../services/appointment';

export type StatutDossier = 'EN_ATTENTE' | 'ACCEPTEE' | 'DECLINEE' | 'DEPASSEE';

@Component({
  selector: 'app-suivi',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './suivi.html',
  styleUrl: './suivi.css',
})
export class Suivi implements OnInit {
  numeroRecherche = '';
  loading = false;
  errorMessage = '';
  demande: SuiviDemandeResponse | null = null;
  statutDossier: StatutDossier = 'EN_ATTENTE';
  derniereActualisation?: Date;

  constructor(
    private route: ActivatedRoute,
    private appointmentService: appointmentService
  ) {}

  ngOnInit(): void {
    // Si un numéro est passé dans l'URL (ex: /suivi?numero=RDV-202610-XXXX), on lance directement la recherche
    this.route.queryParams.subscribe((params) => {
      const num = params['numero'];
      if (num) {
        this.numeroRecherche = num.trim();
        this.rechercher();
      }
    });
  }

  rechercher(): void {
    const num = this.numeroRecherche.trim();
    if (!num) {
      this.errorMessage = 'Veuillez saisir votre numéro de suivi unique (ex: RDV-202610-8A7F1B).';
      this.demande = null;
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.appointmentService.getSuiviDemande(num).subscribe({
      next: (data) => {
        this.loading = false;
        this.demande = data;
        this.derniereActualisation = new Date();
        this.statutDossier = this.evaluerStatut(data);
      },
      error: (error) => {
        this.loading = false;
        this.demande = null;
        if (error.status === 404) {
          this.errorMessage =
            `Aucun dossier trouvé pour le numéro "${num}". Vérifiez l'orthographe de votre identifiant unique.`;
        } else if (error.status === 0) {
          this.errorMessage =
            'Impossible de joindre le serveur. Vérifiez votre connexion ou que le service est actif.';
        } else {
          this.errorMessage =
            error?.error?.message ||
            'Une erreur est survenue lors de la récupération de votre dossier.';
        }
      },
    });
  }

  // Évalue le statut réel en tenant compte du critère des 15 minutes d'expiration
  private evaluerStatut(d: SuiviDemandeResponse): StatutDossier {
    const statutBrut = (d.statut || '').trim().toLowerCase();

    // 1. Si le backend renvoie déjà "Dépassée"
    if (statutBrut.includes('dépass') || statutBrut.includes('depass') || statutBrut.includes('expir')) {
      return 'DEPASSEE';
    }

    // 2. Si le rendez-vous a été accepté / validé, vérification de l'horaire (+15 minutes)
    if (statutBrut.includes('accept') || statutBrut.includes('valid')) {
      if (d.dateConsultation && d.heureConsultation) {
        try {
          const [annee, mois, jour] = d.dateConsultation.split('-').map(Number);
          const [heure, minute] = d.heureConsultation.split(':').map(Number);
          const rdvDate = new Date(annee, mois - 1, jour, heure, minute);

          // Ajout de la tolérance de 15 minutes
          const dateLimite = new Date(rdvDate.getTime() + 15 * 60 * 1000);
          const maintenant = new Date();

          if (maintenant > dateLimite) {
            return 'DEPASSEE';
          }
        } catch (e) {
          console.error("Erreur calcul dépassement d'horaire", e);
        }
      }
      return 'ACCEPTEE';
    }

    // 3. Si refusé / décliné
    if (statutBrut.includes('déclin') || statutBrut.includes('declin') || statutBrut.includes('refus')) {
      return 'DECLINEE';
    }

    // 4. Par défaut : En attente / En cours d'analyse
    return 'EN_ATTENTE';
  }

  actualiser(): void {
    if (this.numeroRecherche) {
      this.rechercher();
    }
  }

  reinitialiser(): void {
    this.numeroRecherche = '';
    this.demande = null;
    this.errorMessage = '';
  }
}
