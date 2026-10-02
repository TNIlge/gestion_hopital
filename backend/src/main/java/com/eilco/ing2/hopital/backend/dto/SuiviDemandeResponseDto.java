package com.eilco.ing2.hopital.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuiviDemandeResponseDto {

    private String numeroDossier;
    private String statut;
    private String nom;
    private String prenom;
    private String departement;
    private String specialite;
    private LocalDate dateSouhaitee;
    private LocalDateTime dateCreation;

    // Informations complémentaires selon l'évolution du statut
    private String motifRefus;                // Présent si statut = Refusée (CARE-203)
    private String medecinNom;                // Présent si statut = Validée (CARE-206)
    private String medecinSpecialite;
    private LocalDate dateConsultation;       // Date attribuée si Validée
    private LocalTime heureConsultation;      // Heure attribuée si Validée
}
