package com.eilco.ing2.hopital.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeRdvRequestDto {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;

    @NotBlank(message = "Le numéro de sécurité sociale est obligatoire")
    private String numeroSecuriteSociale; // Format attendu : nom-prenom-YYYYMMDD (ex: dupont-jean-19951024)

    @NotBlank(message = "Le département est obligatoire")
    private String departement;

    @NotBlank(message = "La spécialité est obligatoire")
    private String specialite;

    @NotNull(message = "La date souhaitée de rendez-vous est obligatoire")
    private LocalDate dateSouhaitee;

    private String motif;
}
