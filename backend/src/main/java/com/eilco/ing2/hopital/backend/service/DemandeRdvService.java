package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;
import com.eilco.ing2.hopital.backend.dto.SuiviDemandeResponseDto;

public interface DemandeRdvService {

    /**
     * CARE-102, CARE-103, CARE-104 :
     * Traite et enregistre une nouvelle demande de rendez-vous.
     * Valide le format du N° SS,
     * génère un numéro de dossier unique,
     * initialise le statut systématiquement à 'En cours',
     * et garantit la non-divulgation du N° SS en retour.
     */
    DemandeRdvResponseDto creerDemandeRdv(DemandeRdvRequestDto requestDto);

    /**
     * CARE-105 : Espace Patient - Suivi de l'état de la demande par son numéro unique de suivi.
     */
    SuiviDemandeResponseDto getSuiviDemande(String numeroDossier);
}
