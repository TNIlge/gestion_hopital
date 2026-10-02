package com.eilco.ing2.hopital.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeRdvResponseDto {

    private String numeroDossier;   // Identifiant unique de suivi (ex: RDV-202609-A8F3)
    private String statut;          // Statut initial : "En attente"
    private String nom;
    private String prenom;
    private String departement;
    private String specialite;
    private LocalDate dateSouhaitee;
    private LocalDateTime dateCreation;
    private String message;
}
