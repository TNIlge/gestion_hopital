package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.ConsultationPlanningDto;
import com.eilco.ing2.hopital.backend.dto.MedecinDto;
import com.eilco.ing2.hopital.backend.dto.PointagePresenceRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface MedecinService {

    /**
     * CARE-301 : Authentification / Identification du médecin par son matricule unique.
     */
    MedecinDto identifierParMatricule(String matricule);

    /**
     * CARE-302 & CARE-303 :
     * Vue "Mon Planning" avec étanchéité stricte des données.
     * Ne renvoie QUE les consultations assignées au médecin et au statut 'Validée' (ou 'Terminée').
     */
    List<ConsultationPlanningDto> getPlanningMedecin(String matricule, LocalDate date);

    /**
     * CARE-304 :
     * Pointage de présence du patient (Présent / Absent) et clôture du dossier.
     */
    ConsultationPlanningDto pointerPresence(Long rendezVousId, PointagePresenceRequestDto requestDto);
}
