import { StatutDemande } from './models';

// Départements et spécialités du cahier des charges
export const DEPARTEMENTS: Record<string, string[]> = {
  Urgences: ['Urgence adulte', 'Urgence pédiatrique', 'Urgence traumatologique'],
  Cardiologie: ['Cardiologie interventionnelle', 'Rythmologie cardiaque', 'Insuffisance cardiaque'],
  'Chirurgie générale': ['Chirurgie digestive et viscérale', 'Chirurgie pariétale', 'Chirurgie de provenance'],
};

export const STATUTS: { code: StatutDemande; libelle: string; badge: string }[] = [
  { code: 'EN_COURS', libelle: 'En cours', badge: 'text-bg-warning' },
  { code: 'ANALYSEE', libelle: 'Analysée', badge: 'text-bg-info' },
  { code: 'VALIDEE', libelle: 'Validée', badge: 'text-bg-success' },
  { code: 'REFUSEE', libelle: 'Refusée', badge: 'text-bg-danger' },
];

/** Deux rendez-vous sont en conflit s'ils tombent dans la même heure du même jour. */
export function memeCreneau(a: string, b: string): boolean {
  return a.slice(0, 13) === b.slice(0, 13);
}
