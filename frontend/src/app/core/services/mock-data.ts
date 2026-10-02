import { Demande, Medecin } from '../models';

// Données fictives utilisées tant que USE_MOCK = true (voir api.config.ts)

export const MEDECINS_MOCK: Medecin[] = [
  { id: 1, numero: 'MED-001', nom: 'Martin', prenom: 'Claire', specialite: 'Urgence adulte' },
  { id: 2, numero: 'MED-002', nom: 'Bernard', prenom: 'Hugo', specialite: 'Urgence adulte' },
  { id: 3, numero: 'MED-003', nom: 'Petit', prenom: 'Léa', specialite: 'Urgence pédiatrique' },
  { id: 4, numero: 'MED-004', nom: 'Robert', prenom: 'Samir', specialite: 'Urgence pédiatrique' },
  { id: 5, numero: 'MED-005', nom: 'Richard', prenom: 'Inès', specialite: 'Urgence traumatologique' },
  { id: 6, numero: 'MED-006', nom: 'Durand', prenom: 'Paul', specialite: 'Urgence traumatologique' },
  { id: 7, numero: 'MED-007', nom: 'Dubois', prenom: 'Sophie', specialite: 'Cardiologie interventionnelle' },
  { id: 8, numero: 'MED-008', nom: 'Moreau', prenom: 'Karim', specialite: 'Cardiologie interventionnelle' },
  { id: 9, numero: 'MED-009', nom: 'Laurent', prenom: 'Emma', specialite: 'Rythmologie cardiaque' },
  { id: 10, numero: 'MED-010', nom: 'Simon', prenom: 'Lucas', specialite: 'Rythmologie cardiaque' },
  { id: 11, numero: 'MED-011', nom: 'Michel', prenom: 'Nadia', specialite: 'Insuffisance cardiaque' },
  { id: 12, numero: 'MED-012', nom: 'Lefebvre', prenom: 'Thomas', specialite: 'Insuffisance cardiaque' },
  { id: 13, numero: 'MED-013', nom: 'Leroy', prenom: 'Julie', specialite: 'Chirurgie digestive et viscérale' },
  { id: 14, numero: 'MED-014', nom: 'Roux', prenom: 'Antoine', specialite: 'Chirurgie digestive et viscérale' },
  { id: 15, numero: 'MED-015', nom: 'David', prenom: 'Camille', specialite: 'Chirurgie pariétale' },
  { id: 16, numero: 'MED-016', nom: 'Bertrand', prenom: 'Youssef', specialite: 'Chirurgie pariétale' },
  { id: 17, numero: 'MED-017', nom: 'Morel', prenom: 'Sarah', specialite: 'Chirurgie de provenance' },
  { id: 18, numero: 'MED-018', nom: 'Fournier', prenom: 'Maxime', specialite: 'Chirurgie de provenance' },
];

export const DEMANDES_MOCK: Demande[] = [
  {
    id: 1, numero: 'RDV-2026-0001', nom: 'Lambert', prenom: 'Alice',
    departement: 'Cardiologie', specialite: 'Cardiologie interventionnelle',
    motif: 'Douleurs thoraciques à l\'effort', dateSouhaitee: '2026-10-06T09:00',
    statut: 'VALIDEE', medecin: { id: 7, nom: 'Dubois', prenom: 'Sophie' },
  },
  {
    // Même créneau et même spécialité que RDV-2026-0001 : assigner Dr Dubois déclenche un conflit
    id: 2, numero: 'RDV-2026-0002', nom: 'Fontaine', prenom: 'Marc',
    departement: 'Cardiologie', specialite: 'Cardiologie interventionnelle',
    motif: 'Contrôle après pose de stent', dateSouhaitee: '2026-10-06T09:00', statut: 'EN_COURS',
  },
  {
    id: 3, numero: 'RDV-2026-0003', nom: 'Girard', prenom: 'Chloé',
    departement: 'Urgences', specialite: 'Urgence pédiatrique',
    motif: 'Fièvre persistante (enfant de 4 ans)', dateSouhaitee: '2026-10-02T14:00', statut: 'EN_COURS',
  },
  {
    id: 4, numero: 'RDV-2026-0004', nom: 'Bonnet', prenom: 'Julien',
    departement: 'Chirurgie générale', specialite: 'Chirurgie pariétale',
    motif: 'Consultation pré-opératoire hernie', dateSouhaitee: '2026-10-08T10:30', statut: 'ANALYSEE',
  },
  {
    id: 5, numero: 'RDV-2026-0005', nom: 'Mercier', prenom: 'Fatima',
    departement: 'Cardiologie', specialite: 'Rythmologie cardiaque',
    motif: 'Palpitations fréquentes', dateSouhaitee: '2026-10-07T11:00', statut: 'EN_COURS',
  },
  {
    id: 6, numero: 'RDV-2026-0006', nom: 'Blanc', prenom: 'Pierre',
    departement: 'Urgences', specialite: 'Urgence traumatologique',
    motif: 'Entorse de la cheville', dateSouhaitee: '2026-10-01T16:00',
    statut: 'VALIDEE', medecin: { id: 5, nom: 'Richard', prenom: 'Inès' },
  },
  {
    id: 7, numero: 'RDV-2026-0007', nom: 'Guerin', prenom: 'Manon',
    departement: 'Chirurgie générale', specialite: 'Chirurgie digestive et viscérale',
    motif: 'Suivi post-opératoire', dateSouhaitee: '2026-10-09T08:30',
    statut: 'REFUSEE', motifRefus: 'Aucun chirurgien disponible sur ce créneau',
  },
  {
    id: 8, numero: 'RDV-2026-0008', nom: 'Muller', prenom: 'Lukas',
    departement: 'Cardiologie', specialite: 'Insuffisance cardiaque',
    motif: 'Essoufflement et œdèmes', dateSouhaitee: '2026-10-05T15:00', statut: 'ANALYSEE',
  },
  {
    id: 9, numero: 'RDV-2026-0009', nom: 'Henry', prenom: 'Zoé',
    departement: 'Urgences', specialite: 'Urgence adulte',
    motif: 'Migraine sévère', dateSouhaitee: '2026-10-02T09:30', statut: 'EN_COURS',
  },
  {
    id: 10, numero: 'RDV-2026-0010', nom: 'Rousseau', prenom: 'Nicolas',
    departement: 'Chirurgie générale', specialite: 'Chirurgie de provenance',
    motif: 'Avis chirurgical', dateSouhaitee: '2026-10-12T13:00',
    statut: 'VALIDEE', medecin: { id: 17, nom: 'Morel', prenom: 'Sarah' },
  },
];
