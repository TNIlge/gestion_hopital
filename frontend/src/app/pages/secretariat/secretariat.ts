import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Demande, Medecin, StatutDemande } from '../../core/models';
import { memeCreneau, STATUTS } from '../../core/referentiel';
import { DemandeService } from '../../core/services/demande.service';

type Filtre = StatutDemande | 'TOUS';

interface Modale {
  type: 'assigner' | 'refuser';
  demande: Demande;
}

@Component({
  selector: 'app-secretariat',
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './secretariat.html',
  styleUrl: './secretariat.css',
})
export class Secretariat implements OnInit {
  private readonly demandeService = inject(DemandeService);

  protected readonly logoDisponible = signal(true);
  protected readonly statuts = STATUTS;
  protected readonly demandes = signal<Demande[]>([]);
  protected readonly chargement = signal(true);
  protected readonly erreurChargement = signal(false);

  // Les demandes "En cours" sont affichées en priorité (roadmap, sprint 2)
  protected readonly filtre = signal<Filtre>('EN_COURS');
  protected readonly recherche = signal('');

  protected readonly compteurs = computed(() => {
    const compte: Record<Filtre, number> = { TOUS: 0, EN_COURS: 0, ANALYSEE: 0, VALIDEE: 0, REFUSEE: 0 };
    for (const d of this.demandes()) {
      compte[d.statut]++;
      compte.TOUS++;
    }
    return compte;
  });

  protected readonly demandesFiltrees = computed(() => {
    const filtre = this.filtre();
    const terme = this.recherche().trim().toLowerCase();
    return this.demandes()
      .filter((d) => filtre === 'TOUS' || d.statut === filtre)
      .filter((d) => !terme || `${d.numero} ${d.nom} ${d.prenom}`.toLowerCase().includes(terme))
      .sort((a, b) => a.dateSouhaitee.localeCompare(b.dateSouhaitee));
  });

  // Modale d'affectation / de refus
  protected readonly modale = signal<Modale | null>(null);
  protected readonly medecins = signal<Medecin[]>([]);
  protected readonly medecinChoisi = signal<number | null>(null);
  protected readonly motifRefus = signal('');
  protected readonly erreurModale = signal<string | null>(null);
  protected readonly envoi = signal(false);

  protected readonly notification = signal<string | null>(null);

  ngOnInit(): void {
    this.charger();
  }

  protected charger(): void {
    this.chargement.set(true);
    this.erreurChargement.set(false);
    this.demandeService.getDemandes().subscribe({
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

  protected statut(code: StatutDemande) {
    return this.statuts.find((s) => s.code === code)!;
  }

  protected estTraitable(d: Demande): boolean {
    return d.statut === 'EN_COURS' || d.statut === 'ANALYSEE';
  }

  /** Indique si un médecin a déjà une consultation validée sur le créneau de la demande. */
  protected estOccupe(medecinId: number, demande: Demande): boolean {
    return this.demandes().some(
      (d) =>
        d.id !== demande.id &&
        d.statut === 'VALIDEE' &&
        d.medecin?.id === medecinId &&
        memeCreneau(d.dateSouhaitee, demande.dateSouhaitee),
    );
  }

  protected marquerAnalysee(d: Demande): void {
    this.demandeService.changerStatut(d.id, 'ANALYSEE').subscribe({
      next: (maj) => {
        this.remplacer(maj);
        this.notifier(`Demande ${maj.numero} marquée comme analysée`);
      },
      error: (e) => this.notifier(this.message(e)),
    });
  }

  protected ouvrirAssignation(d: Demande): void {
    this.ouvrir({ type: 'assigner', demande: d });
    this.medecins.set([]);
    this.demandeService.getMedecins(d.specialite).subscribe((medecins) => this.medecins.set(medecins));
  }

  protected ouvrirRefus(d: Demande): void {
    this.ouvrir({ type: 'refuser', demande: d });
  }

  protected fermer(): void {
    this.modale.set(null);
  }

  protected confirmerAssignation(): void {
    const modale = this.modale();
    const medecinId = this.medecinChoisi();
    if (!modale || medecinId === null) return;

    this.envoi.set(true);
    this.erreurModale.set(null);
    this.demandeService.assignerMedecin(modale.demande.id, medecinId).subscribe({
      next: (maj) => {
        this.remplacer(maj);
        this.fermer();
        this.notifier(`Demande ${maj.numero} validée — Dr ${maj.medecin?.nom}`);
      },
      error: (e: HttpErrorResponse) => {
        this.envoi.set(false);
        this.erreurModale.set(
          e.status === 409 ? `Conflit d'horaire : ${this.message(e)}` : this.message(e),
        );
      },
    });
  }

  protected confirmerRefus(): void {
    const modale = this.modale();
    const motif = this.motifRefus().trim();
    if (!modale || !motif) return;

    this.envoi.set(true);
    this.demandeService.changerStatut(modale.demande.id, 'REFUSEE', motif).subscribe({
      next: (maj) => {
        this.remplacer(maj);
        this.fermer();
        this.notifier(`Demande ${maj.numero} refusée`);
      },
      error: (e) => {
        this.envoi.set(false);
        this.erreurModale.set(this.message(e));
      },
    });
  }

  private ouvrir(modale: Modale): void {
    this.medecinChoisi.set(null);
    this.motifRefus.set('');
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
    return e.error?.message ?? 'Une erreur est survenue, veuillez réessayer.';
  }
}
