package com.eilco.ing2.hopital.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDto {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String specialite;
    private String departement;
    private String email;
    private String telephone;
}
