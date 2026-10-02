// Modèles alignés sur l'API (voir API_DOCUMENTATION_FRONTEND.md).
// Le N° de Sécurité Sociale n'est jamais renvoyé en clair par l'API.

/** Statuts tels que renvoyés par le backend */
export type StatutApi = 'EN_ATTENTE' | 'ACCEPTEE' | 'DECLINEE';

/** États affichés dans l'interface */
export type EtatDemande = 'EN_ATTENTE' | 'VALIDE' | 'DECLINE';

export type StatutPresence = 'NON_DEFINI' | 'PRESENT' | 'ABSENT';

/** Demande vue par le secrétariat (DemandeRdvAdminDto) */
export interface Demande {
  id: number;
  numeroDossier: string;
  nom: string;
  prenom: string;
  dateNaissance: string;
  numeroSecuriteSocialeMasque: string;
  departement: string;
  specialite: string;
  /** YYYY-MM-DD */
  dateSouhaitee: string;
  motif?: string | null;
  statut: StatutApi;
  statutLibelle: string;
  motifRefus?: string | null;
  medecinId?: number | null;
  medecinMatricule?: string | null;
  medecinNom?: string | null;
  medecinPrenom?: string | null;
  /** YYYY-MM-DD */
  dateConsultation?: string | null;
  /** HH:mm ou HH:mm:ss selon le backend */
  heureConsultation?: string | null;
  statutPresence: StatutPresence;
  dateCreation: string;
  dateMiseAJour: string;
}

export interface Medecin {
  id: number;
  matricule: string;
  nom: string;
  prenom: string;
  specialite: string;
  departement: string;
  email?: string;
  telephone?: string;
}

export interface Affectation {
  medecinId: number;
  dateConsultation: string;
  heureConsultation: string;
}

/** Consultation vue par le médecin (ConsultationPlanningDto) */
export interface ConsultationPlanning {
  id: number;
  numeroDossier: string;
  nomPatient: string;
  prenomPatient: string;
  dateNaissancePatient: string;
  motif?: string | null;
  dateConsultation: string;
  heureConsultation: string;
  specialite: string;
  statut: string;
  statutPresence: StatutPresence;
  datePointage?: string | null;
}

export type Role = 'SECRETAIRE' | 'MEDECIN';

export interface Session {
  role: Role;
  nom: string;
  prenom: string;
  /** Renseigné uniquement pour un médecin */
  medecinId?: number;
  matricule?: string;
}
