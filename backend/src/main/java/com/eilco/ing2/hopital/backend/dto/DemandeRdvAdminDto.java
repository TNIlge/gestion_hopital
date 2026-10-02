package com.eilco.ing2.hopital.backend.dto;

import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.model.StatutPresence;
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
public class DemandeRdvAdminDto {

    private Long id;
    private String numeroDossier;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String numeroSecuriteSocialeMasque; // XXXXXXXXXXXXX
    private String departement;
    private String specialite;
    private LocalDate dateSouhaitee;
    private String motif;
    private StatutDemande statut;
    private String statutLibelle;
    private String motifRefus;

    // Informations du médecin affecté le cas échéant
    private Long medecinId;
    private String medecinMatricule;
    private String medecinNom;
    private String medecinPrenom;

    private LocalDate dateConsultation;
    private LocalTime heureConsultation;
    private StatutPresence statutPresence;

    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
}
