// =============================================================================
//  Configuration de l'accès aux données
// =============================================================================
//
//  USE_MOCK = true  → données fictives en mémoire (DemandeMockService)
//  USE_MOCK = false → appels HTTP vers le backend Spring (DemandeHttpService)
//
//  Contrat d'API attendu côté backend :
//    GET   /api/demandes                    → Demande[]
//    PATCH /api/demandes/{id}/statut        body { statut, motifRefus? } → Demande
//    PUT   /api/demandes/{id}/medecin       body { medecinId }           → Demande (statut VALIDEE)
//                                           409 + { message } si le médecin a déjà
//                                           une consultation validée sur ce créneau
//    GET   /api/medecins?specialite=...     → Medecin[]
//
//  Les formats JSON sont ceux de src/app/core/models.ts.
// =============================================================================

export const USE_MOCK = true;

export const API_URL = 'http://localhost:8080/api';
