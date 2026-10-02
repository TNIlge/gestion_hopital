import { EtatDemande, StatutApi } from './models';

export const ETATS: { code: EtatDemande; libelle: string; badge: string }[] = [
  { code: 'EN_ATTENTE', libelle: 'En attente', badge: 'text-bg-warning' },
  { code: 'VALIDE', libelle: 'Validé', badge: 'text-bg-success' },
  { code: 'DECLINE', libelle: 'Décliné', badge: 'text-bg-danger' },
];

/** Correspondance entre les statuts du backend et les états affichés. */
export function etatDe(statut: StatutApi): EtatDemande {
  switch (statut) {
    case 'ACCEPTEE':
      return 'VALIDE';
    case 'DECLINEE':
      return 'DECLINE';
    default:
      return 'EN_ATTENTE';
  }
}

/** Le backend renvoie les heures en HH:mm:ss ; l'interface les manipule en HH:mm. */
export function hhmm(heure: string | null | undefined): string {
  return heure ? heure.slice(0, 5) : '';
}
