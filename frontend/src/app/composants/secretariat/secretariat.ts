import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Demande, EtatDemande, Medecin } from '../../core/models';
import { ETATS, etatDe, hhmm } from '../../core/referentiel';
import { SecretariatService } from '../../core/services/secretariat.service';
import { EnteteEspace } from '../entete-espace/entete-espace';

type Filtre = EtatDemande | 'TOUS';

interface Modale {
  type: 'assigner' | 'decliner';
  demande: Demande;
}

const HEURE_PAR_DEFAUT = '09:00';

@Component({
  selector: 'app-secretariat',
  imports: [DatePipe, EnteteEspace, FormsModule],
  templateUrl: './secretariat.html',
  styleUrl: './secretariat.css',
})
export class Secretariat implements OnInit {
  private readonly secretariatService = inject(SecretariatService);

  protected readonly etats = ETATS;
  protected readonly hhmm = hhmm;
  protected readonly demandes = signal<Demande[]>([]);
  protected readonly chargement = signal(true);
  protected readonly erreurChargement = signal(false);

  // Les demandes "En attente" sont affichées en priorité (roadmap, sprint 2)
  protected readonly filtre = signal<Filtre>('EN_ATTENTE');
  protected readonly recherche = signal('');

  protected readonly compteurs = computed(() => {
    const compte: Record<Filtre, number> = { TOUS: 0, EN_ATTENTE: 0, VALIDE: 0, DECLINE: 0 };
    for (const d of this.demandes()) {
      compte[etatDe(d.statut)]++;
      compte.TOUS++;
    }
    return compte;
  });

  protected readonly demandesFiltrees = computed(() => {
    const filtre = this.filtre();
    const terme = this.recherche().trim().toLowerCase();
    return this.demandes()
      .filter((d) => filtre === 'TOUS' || etatDe(d.statut) === filtre)
      .filter((d) => !terme || `${d.numeroDossier} ${d.nom} ${d.prenom}`.toLowerCase().includes(terme))
      .sort((a, b) => a.dateSouhaitee.localeCompare(b.dateSouhaitee));
  });

  // Modale d'affectation / de déclin
  protected readonly modale = signal<Modale | null>(null);
  protected readonly medecins = signal<Medecin[] | null>(null);
  protected readonly medecinChoisi = signal<number | null>(null);
  protected readonly dateConsultation = signal('');
  protected readonly heureConsultation = signal(HEURE_PAR_DEFAUT);
  protected readonly motifDeclin = signal('');
  protected readonly erreurModale = signal<string | null>(null);
  protected readonly envoi = signal(false);

  protected readonly notification = signal<string | null>(null);

  ngOnInit(): void {
    this.charger();
  }

  protected charger(): void {
    this.chargement.set(true);
    this.erreurChargement.set(false);
    this.secretariatService.getDemandes().subscribe({
      next: (demandes) => {
        this.demandes.set(demandes);
        this.chargement.set(false);
      },
      error: () => {
        this.erreurChargement.set(true);
        this.chargement.set(false);
      },
    });
  }

  protected etat(d: Demande) {
    const code = etatDe(d.statut);
    return this.etats.find((e) => e.code === code)!;
  }

  protected estEnAttente(d: Demande): boolean {
    return etatDe(d.statut) === 'EN_ATTENTE';
  }

  /** Indique si un médecin a déjà une consultation validée sur le créneau choisi. */
  protected estOccupe(medecinId: number, demande: Demande): boolean {
    return this.demandes().some(
      (d) =>
        d.id !== demande.id &&
        d.statut === 'ACCEPTEE' &&
        d.medecinId === medecinId &&
        d.dateConsultation === this.dateConsultation() &&
        hhmm(d.heureConsultation) === this.heureConsultation(),
    );
  }

  protected ouvrirAssignation(d: Demande): void {
    this.ouvrir({ type: 'assigner', demande: d });
    this.dateConsultation.set(d.dateSouhaitee);
    this.heureConsultation.set(HEURE_PAR_DEFAUT);
    this.medecins.set(null);
    this.secretariatService.getMedecins(d.specialite).subscribe({
      next: (medecins) => this.medecins.set(medecins),
      error: (e) => {
        this.medecins.set([]);
        this.erreurModale.set(this.message(e));
      },
    });
  }

  protected ouvrirDeclin(d: Demande): void {
    this.ouvrir({ type: 'decliner', demande: d });
  }

  protected fermer(): void {
    this.modale.set(null);
  }

  protected confirmerAssignation(): void {
    const modale = this.modale();
    const medecinId = this.medecinChoisi();
    if (!modale || medecinId === null || !this.dateConsultation() || !this.heureConsultation()) return;

    this.envoi.set(true);
    this.erreurModale.set(null);
    this.secretariatService
      .affecter(modale.demande.id, {
        medecinId,
        dateConsultation: this.dateConsultation(),
        heureConsultation: this.heureConsultation(),
      })
      .subscribe({
        next: (maj) => {
          this.remplacer(maj);
          this.fermer();
          this.notifier(`Demande ${maj.numeroDossier} validée avec Dr ${maj.medecinNom}`);
        },
        error: (e: HttpErrorResponse) => {
          this.envoi.set(false);
          this.erreurModale.set(this.message(e));
        },
      });
  }

  protected confirmerDeclin(): void {
    const modale = this.modale();
    const motif = this.motifDeclin().trim();
    if (!modale || !motif) return;

    this.envoi.set(true);
    this.secretariatService.refuser(modale.demande.id, motif).subscribe({
      next: (maj) => {
        this.remplacer(maj);
        this.fermer();
        this.notifier(`Demande ${maj.numeroDossier} déclinée`);
      },
      error: (e) => {
        this.envoi.set(false);
        this.erreurModale.set(this.message(e));
      },
    });
  }

  private ouvrir(modale: Modale): void {
    this.medecinChoisi.set(null);
    this.motifDeclin.set('');
    this.erreurModale.set(null);
    this.envoi.set(false);
    this.modale.set(modale);
  }

  private remplacer(maj: Demande): void {
    this.demandes.update((liste) => liste.map((d) => (d.id === maj.id ? maj : d)));
  }

  private notifier(message: string): void {
    this.notification.set(message);
    setTimeout(() => this.notification.set(null), 3500);
  }

  private message(e: HttpErrorResponse): string {
    if (e.status === 0) return 'Serveur injoignable. Vérifiez que le backend est démarré.';
    return e.error?.message ?? 'Une erreur est survenue, veuillez réessayer.';
  }
}
