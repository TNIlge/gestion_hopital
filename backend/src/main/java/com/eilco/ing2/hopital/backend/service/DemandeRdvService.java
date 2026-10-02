package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;

public interface DemandeRdvService {

    /**
     * Traite et enregistre une nouvelle demande de rendez-vous.
     * Valide le format du N° SS (nom-prenom-YYYYMMDD),
     * génère un numéro de dossier unique,
     * et initialise le statut à 'En attente'.
     */
    DemandeRdvResponseDto creerDemandeRdv(DemandeRdvRequestDto requestDto);
}
