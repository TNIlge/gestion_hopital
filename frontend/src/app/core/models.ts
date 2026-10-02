// Modèles partagés entre le front et l'API.
// Le N° de Sécurité Sociale n'apparaît volontairement dans aucun modèle de lecture.

export type StatutDemande = 'EN_COURS' | 'ANALYSEE' | 'VALIDEE' | 'REFUSEE';

export interface MedecinResume {
  id: number;
  nom: string;
  prenom: string;
}

export interface Medecin extends MedecinResume {
  numero: string;
  specialite: string;
}

export interface Demande {
  id: number;
  numero: string;
  nom: string;
  prenom: string;
  departement: string;
  specialite: string;
  motif: string;
  /** Créneau souhaité, format ISO local : "2026-10-06T09:00" */
  dateSouhaitee: string;
  statut: StatutDemande;
  medecin?: MedecinResume;
  motifRefus?: string;
}
