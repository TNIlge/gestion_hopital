// =============================================================================
//  Accès au backend Spring (contrat : API_DOCUMENTATION_FRONTEND.md,
//  statuts mis à jour côté backend : EN_ATTENTE / ACCEPTEE / DECLINEE)
// =============================================================================
//
//  Endpoints utilisés :
//    GET   /secretariat/demandes                 liste des demandes
//    PATCH /secretariat/demandes/{id}/refuser    body { motifRefus }
//    GET   /secretariat/medecins?specialite=     médecins d'une spécialité
//    POST  /secretariat/demandes/{id}/affecter   body { medecinId, dateConsultation, heureConsultation }
//                                                400 "Conflit d'agenda" si le créneau est pris
//    POST  /medecin/auth                         body { matricule } → profil, 404 si inconnu
//    GET   /medecin/{matricule}/planning         consultations acceptées du médecin
//    PUT   /medecin/rendez-vous/{id}/presence    body { presence: "PRESENT" | "ABSENT" }
// =============================================================================

export const API_URL = 'http://localhost:8080/api/v1';
