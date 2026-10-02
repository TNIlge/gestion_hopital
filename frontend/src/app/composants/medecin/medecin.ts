import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { ConsultationPlanning, StatutPresence } from '../../core/models';
import { AuthService } from '../../core/services/auth.service';
import { hhmm } from '../../core/referentiel';
import { MedecinService } from '../../core/services/medecin.service';
import { EnteteEspace } from '../entete-espace/entete-espace';

type Periode = 'AUJOURDHUI' | 'A_VENIR' | 'PASSES';
type EtatTemporel = 'A_VENIR' | 'EN_CONSULTATION' | 'TERMINE';

/** Durée d'un créneau, cohérente avec la règle de conflit (une consultation par heure). */
const DUREE_CRENEAU_MS = 60 * 60 * 1000;
const RAFRAICHISSEMENT_MS = 30 * 1000;

@Component({
  selector: 'app-medecin',
  imports: [DatePipe, EnteteEspace],
  templateUrl: './medecin.html',
  styleUrl: './medecin.css',
})
export class MedecinPlanning implements OnInit {
  private readonly medecinService = inject(MedecinService);
  protected readonly session = inject(AuthService).session;

  protected readonly rendezVous = signal<ConsultationPlanning[]>([]);
  protected readonly chargement = signal(true);
  protected readonly erreurChargement = signal(false);
  protected readonly periode = signal<Periode>('AUJOURDHUI');
  protected readonly hhmm = hhmm;
  protected readonly notification = signal<string | null>(null);

  // Horloge : les états "À venir / En consultation / Terminé" évoluent avec l'heure
  protected readonly maintenant = signal(Date.now());

  protected readonly periodes: { code: Periode; libelle: string }[] = [
    { code: 'AUJOURDHUI', libelle: "Aujourd'hui" },
    { code: 'A_VENIR', libelle: 'À venir' },
    { code: 'PASSES', libelle: 'Passés' },
  ];

  private readonly aujourdhui = computed(() => cleJour(new Date(this.maintenant())));

  protected readonly duJour = computed(() =>
    this.rendezVous().filter((r) => r.dateConsultation === this.aujourdhui()),
  );

  protected readonly stats = computed(() => {
    const jour = this.duJour();
    return {
      total: jour.length,
      presents: jour.filter((r) => r.statutPresence === 'PRESENT').length,
      absents: jour.filter((r) => r.statutPresence === 'ABSENT').length,
      aPointer: jour.filter((r) => r.statutPresence === 'NON_DEFINI' && this.etat(r) !== 'A_VENIR').length,
    };
  });

  /** Rendez-vous de la période choisie, regroupés par jour. */
  protected readonly groupes = computed(() => {
    const jour = this.aujourdhui();
    const periode = this.periode();
    const selection = this.rendezVous()
      .filter((r) => {
        const cle = r.dateConsultation;
        if (periode === 'AUJOURDHUI') return cle === jour;
        return periode === 'A_VENIR' ? cle > jour : cle < jour;
      })
      .sort((a, b) => debut(a) - debut(b));
    if (periode === 'PASSES') selection.reverse();

    const groupes: { jour: string; rendezVous: ConsultationPlanning[] }[] = [];
    for (const r of selection) {
      const cle = r.dateConsultation;
      const dernier = groupes.at(-1);
      if (dernier?.jour === cle) dernier.rendezVous.push(r);
      else groupes.push({ jour: cle, rendezVous: [r] });
    }
    return groupes;
  });

  constructor() {
    const horloge = setInterval(() => this.maintenant.set(Date.now()), RAFRAICHISSEMENT_MS);
    inject(DestroyRef).onDestroy(() => clearInterval(horloge));
  }

  ngOnInit(): void {
    this.charger();
  }

  protected charger(): void {
    const matricule = this.session()?.matricule;
    if (!matricule) return;

    this.chargement.set(true);
    this.erreurChargement.set(false);
    this.medecinService.getPlanning(matricule).subscribe({
      next: (rendezVous) => {
        this.rendezVous.set(rendezVous);
        this.chargement.set(false);
      },
      error: () => {
        this.erreurChargement.set(true);
        this.chargement.set(false);
      },
    });
  }

  protected etat(r: ConsultationPlanning): EtatTemporel {
    const heure = debut(r);
    const maintenant = this.maintenant();
    if (maintenant < heure) return 'A_VENIR';
    return maintenant < heure + DUREE_CRENEAU_MS ? 'EN_CONSULTATION' : 'TERMINE';
  }

  protected pointer(r: ConsultationPlanning, presence: StatutPresence): void {
    if (r.statutPresence === presence) return;
    this.medecinService.pointerPresence(r.id, presence).subscribe({
      next: (maj) => {
        this.rendezVous.update((liste) => liste.map((d) => (d.id === maj.id ? maj : d)));
        this.notifier(`${maj.prenomPatient} ${maj.nomPatient} : ${presence === 'PRESENT' ? 'présent' : 'absent'}`);
      },
      error: (e: HttpErrorResponse) => this.notifier(e.error?.message ?? 'Le pointage a échoué'),
    });
  }

  private notifier(message: string): void {
    this.notification.set(message);
    setTimeout(() => this.notification.set(null), 3000);
  }
}

function debut(r: ConsultationPlanning): number {
  return new Date(`${r.dateConsultation}T${hhmm(r.heureConsultation)}`).getTime();
}

function cleJour(d: Date): string {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}
