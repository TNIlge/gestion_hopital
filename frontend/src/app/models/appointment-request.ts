export interface AppointmentRequest {
  nom: string;
  prenom: string;
  dateNaissance: string;
  numeroSecuriteSociale: string;
  departement: string;
  specialite: string;
  dateSouhaitee: string;
  motif?: string;
}

export interface AppointmentResponse {
  numeroDossier?: string;
  message?: string;
  nom?: string;
  prenom?: string;
  departement?: string;
  specialite?: string;
  dateSouhaitee?: string;
  dateCreation?: string;
  statut?: string;
}
