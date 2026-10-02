package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.AffectationMedecinRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvAdminDto;
import com.eilco.ing2.hopital.backend.dto.MedecinDto;
import com.eilco.ing2.hopital.backend.dto.RefusDemandeRequestDto;
import com.eilco.ing2.hopital.backend.model.StatutDemande;

import java.time.LocalDate;
import java.util.List;

public interface SecretariatService {

    /**
     * CARE-201 & CARE-202 : Tableau de bord secrétariat avec filtres optionnels multi-critères.
     */
    List<DemandeRdvAdminDto> getDemandes(StatutDemande statut, String specialite, LocalDate date);

    /**
     * Récupère une demande par son id.
     */
    DemandeRdvAdminDto getDemandeById(Long id);

    /**
     * CARE-203 : Passage au statut 'Analysée'.
     */
    DemandeRdvAdminDto passerEnAnalysee(Long id);

    /**
     * CARE-203 : Refus de la demande avec motif obligatoire enregistré en BDD.
     */
    DemandeRdvAdminDto refuserDemande(Long id, RefusDemandeRequestDto requestDto);

    /**
     * CARE-204 : Récupération des médecins habilités pour une spécialité donnée.
     */
    List<MedecinDto> getMedecinsBySpecialite(String specialite);

    /**
     * CARE-205 & CARE-206 :
     * Algorithme anti-conflit d'agenda et affectation formelle avec statut 'Validée'.
     * Lève AgendaConflictException (HTTP 400) en cas de double réservation.
     */
    DemandeRdvAdminDto affecterMedecin(Long demandeId, AffectationMedecinRequestDto requestDto);
}
